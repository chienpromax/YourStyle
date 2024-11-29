package yourstyle.com.shope.controller.site;

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
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.validation.site.AccountValidator;

@Controller("siteAccountController")
@RequestMapping("/yourstyle/accounts")
public class AccountController {

	@Autowired
	private AccountService accountService;
		@Autowired
	private AccountValidator accountValidator;

	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("account", new Account());
		return "site/accounts/register";
	}
@PostMapping("/register")
	public String register(@ModelAttribute("account") Account newAccount, @RequestParam String confirmPassword,
			BindingResult bindingResult, Model model) {

		// Gọi validator để kiểm tra dữ liệu đầu vào
		accountValidator.validate(newAccount, bindingResult);

		// Kiểm tra nếu có lỗi
		if (bindingResult.hasErrors()) {
			model.addAttribute("errorMessage", "Thông tin nhập vào không hợp lệ. Vui lòng kiểm tra lại!");
			return "site/accounts/register";
		}

		// Xử lý đăng ký nếu không có lỗi
		try {
			accountService.register(newAccount, confirmPassword);
			model.addAttribute("message", "Đăng ký thành công!");
			return "site/accounts/login";
		} catch (IllegalArgumentException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "site/accounts/register";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Có lỗi xảy ra khi tạo khách hàng!" + e.getMessage());
			return "site/accounts/register";
		}
	}

	@GetMapping("/forgotpassword")
	public String showForgotPasswordForm() {
		return "site/accounts/forgotpassword";
	}

	@PostMapping("/processforgotpassword")
	public String forgotPassword(@RequestParam("email") String email, Model model) {
		if (email == null || email.isEmpty()) {
			model.addAttribute("errorMessage", "Vui lòng nhập địa chỉ email.");
			return "site/accounts/forgotpassword";
		}

		try {
			accountService.sendResetPasswordLink(email);
			model.addAttribute("message", "Liên kết đặt lại mật khẩu đã được gửi đến email của bạn.");
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Không tìm thấy tài khoản với email này.");
		}
		return "site/accounts/forgotpassword";
	}

	@GetMapping("/resetpassword")
	public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
		if (token == null || token.isEmpty()) {
			model.addAttribute("errorMessage", "Token không hợp lệ.");
			return "error"; //
		}
		model.addAttribute("token", token);
		return "site/accounts/resetpassword";
	}

	@PostMapping("/resetpassword")
	public String resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword, RedirectAttributes redirectAttributes,
			Model model) {
		try {
		
			if (!newPassword.equals(confirmPassword)) {
				throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
			}

			// String encodedPassword = passwordEncoder.encode(newPassword); // Sử dụng passwordEncoder
			accountService.resetPassword(token, newPassword, confirmPassword); // Gọi phương thức với 3 tham số

			redirectAttributes.addFlashAttribute("message", "Đặt lại mật khẩu thành công!");
			return "redirect:/yourstyle/accounts/login";
		} catch (IllegalArgumentException e) {
			model.addAttribute("token", token);
			model.addAttribute("error", e.getMessage());
			return "site/accounts/resetpassword";
		} catch (Exception e) {
			model.addAttribute("token", token);
			model.addAttribute("error", "Có lỗi xảy ra trong quá trình đặt lại mật khẩu.");
			return "site/accounts/resetpassword";
		}
	}
}
