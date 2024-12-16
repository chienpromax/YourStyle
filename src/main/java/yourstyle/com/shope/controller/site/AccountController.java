package yourstyle.com.shope.controller.site;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.OTPToken;
import yourstyle.com.shope.repository.OTPRepository;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.validation.site.AccountValidator;

@Controller("siteAccountController")
@RequestMapping("/yourstyle/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountValidator accountValidator;
    // new
    @Autowired
    private OTPRepository otpRepository;
    @Autowired
    private HttpSession session;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("account", new Account());
        return "site/accounts/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("account") Account newAccount, @RequestParam String confirmPassword, BindingResult bindingResult, Model model, HttpSession session) {

        accountValidator.validate(newAccount, bindingResult);

        if (bindingResult.hasErrors()) {
            // In ra tất cả các lỗi
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println("Lỗi: " + error.getDefaultMessage());
            });

            // Nối tất cả các lỗi lại với nhau nếu có nhiều lỗi, sau đó truyền vào model
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage()) // Lấy thông báo lỗi
                    .collect(Collectors.joining(", ")); // Nối các lỗi với dấu phẩy
            model.addAttribute("errorMessage", errorMessage);  // Truyền thông báo lỗi vào model
            return "site/accounts/register";
        }

        if (accountService.existsByUsername(newAccount.getUsername())) {
            model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
            return "site/accounts/register";  // Trả về trang đăng ký với lỗi tên đăng nhập
        }

        session.setAttribute("newAccount", newAccount);
        session.setAttribute("confirmPassword", confirmPassword);

        accountService.sendOTP(newAccount.getEmail(), session);
        model.addAttribute("message", "Mã OTP đã được gửi đến email của bạn.");
        return "site/accounts/verifyOTP";  // Chuyển hướng tới trang nhập OTP
    }

    @PostMapping("/verifyOTP")
    public String verifyOTP(@RequestParam String otp, Model model, HttpSession session) {
        String sessionOtp = (String) session.getAttribute("otp"); // Lấy OTP từ session

        if (sessionOtp != null && sessionOtp.equals(otp)) {
            Optional<OTPToken> otpTokenOptional = otpRepository.findByOtp(otp);

            if (otpTokenOptional.isPresent()) {
                OTPToken otpToken = otpTokenOptional.get();
                // Kiểm tra thời gian hết hạn của OTP
                if (otpToken.getExpiryTime().isAfter(LocalDateTime.now())) {
                    Account newAccount = (Account) session.getAttribute("newAccount");
                    String confirmPassword = (String) session.getAttribute("confirmPassword");

                    try {
                        // Lưu thông tin tài khoản vào cơ sở dữ liệu
                        accountService.register(newAccount, confirmPassword);
                        model.addAttribute("message", "Xác thực OTP thành công và tài khoản đã được tạo!");
                        session.removeAttribute("newAccount");  // Xóa thông tin trong session sau khi đã lưu vào cơ sở dữ liệu
                        session.removeAttribute("confirmPassword");

                        return "redirect:/yourstyle/accounts/login";
                    } catch (Exception e) {
                        model.addAttribute("errorMessage", "Có lỗi xảy ra khi tạo tài khoản: " + e.getMessage());
                        return "site/accounts/verifyOTP";
                    }
                } else {
                    model.addAttribute("errorMessage", "Mã OTP đã hết hạn.");
                    return "site/accounts/verifyOTP";
                }
            } else {
                model.addAttribute("errorMessage", "Mã OTP không chính xác.");
                return "site/accounts/verifyOTP";
            }
        } else {
            model.addAttribute("errorMessage", "Mã OTP không chính xác hoặc đã hết hạn.");
            return "site/accounts/verifyOTP";
        }
    }


    // Phương thức xử lý gửi OTP
    @PostMapping("/sendOTP")
    public String sendOTP(@RequestParam("email") String email, HttpSession session, Model model) {
        try {
            accountService.sendOTP(email, session);
            model.addAttribute("message", "OTP đã được gửi vào email của bạn.");
            return "site/accounts/verifyOTP";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi gửi OTP.");
            return "site/accounts/register";
        }
    }


    // Phương thức gửi lại OTP
    @PostMapping("/resendOTP")
    public String resendOTP(@RequestParam("email") String email, HttpSession session, Model model) {
        try {
            // Gửi OTP qua email và lưu vào session
            String otp = accountService.sendOTP(email, session);
            model.addAttribute("message", "OTP đã được gửi lại vào email của bạn.");
            return "site/accounts/verifyOTP";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi gửi OTP.");
            return "site/accounts/verifyOTP";
        }
    }


//FORGOT PASS

    // Hiển thị trang quên mật khẩu
    @GetMapping("/forgotpassword")
    public String showForgotPasswordForm() {
        return "site/accounts/forgotpassword";
    }

    // Xử lý quên mật khẩu (Gửi OTP qua email)
    @PostMapping("/processforgotpassword")
    public String forgotPassword(@RequestParam("email") String email, Model model) {
        if (email == null || email.isEmpty()) {
            model.addAttribute("errorMessage", "Vui lòng nhập địa chỉ email.");
            return "site/accounts/forgotpassword";
        }
        // Lưu email vào session
        session.setAttribute("email", email);
        try {
            accountService.sendOTPForPasswordReset(email); // Gửi OTP cho email
            model.addAttribute("message", "Mã OTP đã được gửi đến email của bạn để xác thực.");
            return "site/accounts/verifyOTPForPasswordReset";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Không tìm thấy tài khoản với email này.");
            return "site/accounts/forgotpassword";
        }
    }

    // Xác thực OTP để tiến hành reset mật khẩu
    @PostMapping("/verifyOTPForPasswordReset")
    public String verifyOTPForPasswordReset(@RequestParam("otp") String otp, Model model, HttpSession session) {
        try {
            boolean isOTPValid = accountService.verifyOTPForPasswordReset(otp); // Xác thực OTP

            if (isOTPValid) {
                model.addAttribute("otp", otp);
                return "site/accounts/resetpassword";
            } else {
                model.addAttribute("errorMessage", "Mã OTP không chính xác.");
                return "site/accounts/verifyOTPForPasswordReset";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi xác thực OTP.");
            return "site/accounts/verifyOTPForPasswordReset";
        }
    }

    // Phương thức gửi lại OTP cho chức năng quên mật khẩu
    @PostMapping("/resendOTPForPasswordReset")
    public String resendOTPForPasswordReset(@RequestParam("email") String email, HttpSession session, Model model) {
        try {
            String otp = accountService.sendOTPForPasswordReset(email);
            model.addAttribute("message", "OTP đã được gửi lại vào email của bạn để xác thực thay đổi mật khẩu.");
            return "site/accounts/verifyOTPForPasswordReset";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi gửi lại mã OTP. Vui lòng thử lại.");
            return "site/accounts/verifyOTPForPasswordReset";
        }
    }
    //change pass

    // Trang nhập mật khẩu mới
    @GetMapping("/resetpassword")
    public String showResetPasswordForm(Model model) {
        // Lấy email từ session
        String email = (String) session.getAttribute("email");

        // Kiểm tra xem email có hợp lệ không
        if (email == null || email.isEmpty()) {
            model.addAttribute("error", "Email không hợp lệ.");
            return "error"; // Nếu không hợp lệ, hiển thị trang lỗi
        }

        model.addAttribute("email", email);
        return "site/accounts/resetpassword";  // Trang nhập mật khẩu mới
    }

    // Xử lý reset mật khẩu
    @PostMapping("/resetpassword")
    public String resetPassword(@RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
                return "site/accounts/resetpassword"; // Quay lại form reset mật khẩu
            }

            // Lấy email từ session
            String email = (String) session.getAttribute("email");

            if (email == null || email.isEmpty()) {
                model.addAttribute("error", "Không tìm thấy email.");
                return "error"; // Hiển thị trang lỗi nếu không có email
            }

            // Gọi service để reset mật khẩu
            accountService.resetPasswordByEmail(email, newPassword);

            // Xóa email khỏi session sau khi reset thành công
            session.removeAttribute("email");

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("message", "Đặt lại mật khẩu thành công!");
            return "redirect:/yourstyle/accounts/login"; // Redirect đến trang đăng nhập
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "site/accounts/resetpassword"; // Quay lại form reset mật khẩu
        }
    }


}
