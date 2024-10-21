package yourstyle.com.shope.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.service.AccountService;

@Controller("siteAccountController")
@RequestMapping("/yourstyle/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("account", new Account());
        return "site/accounts/register"; 
    }

    @PostMapping("/register") 
    public String register(Account account, String confirmPassword, Model model) {
        // Kiểm tra mật khẩu và xác nhận mật khẩu
        try {
            accountService.register(account, confirmPassword);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "site/accounts/register"; // Trả về trang đăng ký với thông báo lỗi
        }

        return "site/accounts/login"; // Chuyển hướng đến trang đăng nhập
    }

    // Hiển thị form quên mật khẩu
    @GetMapping("/forgotpassword")
    public String showForgotPasswordForm() {
        return "site/accounts/forgotpassword"; 
    }

    @PostMapping("/processforgotpassword") // Thay đổi đường dẫn
    public String forgotPassword(@RequestParam("email") String email, Model model) {
        // Kiểm tra định dạng email hợp lệ
        if (email == null || email.isEmpty()) {
            model.addAttribute("errorMessage", "Vui lòng nhập địa chỉ email.");
            return "site/accounts/forgotpassword"; // Trả về trang quên mật khẩu với thông báo lỗi
        }

        try {
            // Gọi phương thức từ AccountService
            accountService.sendResetPasswordLink(email);
            model.addAttribute("message", "Liên kết đặt lại mật khẩu đã được gửi đến email của bạn.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Không tìm thấy tài khoản với email này.");
        }
        return "site/accounts/forgotpassword"; // Trả về trang quên mật khẩu với thông báo
    }

    @GetMapping("/resetpassword")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (token == null || token.isEmpty()) {
            model.addAttribute("errorMessage", "Token không hợp lệ.");
            return "error"; // Trả về một trang lỗi hoặc trang chính
        }
        model.addAttribute("token", token);
        return "site/accounts/resetpassword"; 
    }
    @PostMapping("/resetpassword")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            // Xử lý reset mật khẩu
            accountService.resetPassword(token, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("message", "Đặt lại mật khẩu thành công!");
            return "redirect:/yourstyle/accounts/login"; 
        } catch (IllegalArgumentException e) {
            model.addAttribute("token", token); // Truyền lại token
            model.addAttribute("error", e.getMessage()); // Thêm lỗi vào model
            return "site/accounts/resetpassword"; // Trả về template resetpassword
        }
    }


    }

