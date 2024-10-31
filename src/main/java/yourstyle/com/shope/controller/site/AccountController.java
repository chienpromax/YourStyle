package yourstyle.com.shope.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Role;
import yourstyle.com.shope.repository.AccountRepository;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.service.AccountService;

@Controller("siteAccountController")
@RequestMapping("/yourstyle/accounts")
public class AccountController {
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountRepository accountRepository;

	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("account", new Account());
		return "site/accounts/register";
	}

//
//	@PostMapping("/register")
//	public String register(@ModelAttribute("account") Account newAccount,
//	                       @RequestParam String confirmPassword,
//	                       Model model) {
//	    if (!newAccount.getPassword().equals(confirmPassword)) {
//	        model.addAttribute("errorMessage", "Mật khẩu và xác nhận mật khẩu không khớp!");
//	        return "site/accounts/register"; 
//	    }
//
//	    if (accountService.existsByEmail(newAccount.getEmail())) {
//	        model.addAttribute("errorMessage", "Email này đã được sử dụng, vui lòng chọn email khác!");
//	        return "site/accounts/register"; 
//	    }
//
//	    try {
//	        Role role = new Role();
//	        role.setRoleId(1); 
//	        newAccount.setRole(role);
//
//	        Account savedAccount = accountService.save(newAccount); 
//	        Customer newCustomer = new Customer();
//	        newCustomer.setAccount(savedAccount);
//	        customerRepository.save(newCustomer); 
//
//	        model.addAttribute("message", "Đăng ký thành công!");
//	        return "site/accounts/login"; 
//	    } catch (IllegalArgumentException e) {
//	        model.addAttribute("errorMessage", e.getMessage());
//	        return "site/accounts/register"; 
//	    }
//	}
	@PostMapping("/register")
	public String register(@ModelAttribute("account") Account newAccount, @RequestParam String confirmPassword,
			Model model) {
		if (!newAccount.getPassword().equals(confirmPassword)) {
			model.addAttribute("errorMessage", "Mật khẩu và xác nhận mật khẩu không khớp!");
			return "site/accounts/register";
		}

		// Kiểm tra trùng username
		if (accountService.existsByUsername(newAccount.getUsername())) {
			model.addAttribute("errorMessage", "Tên người dùng đã tồn tại!");
			return "site/accounts/register";
		}

		try {
			Role role = new Role();
			role.setRoleId(1);
			newAccount.setRole(role);
			Account savedAccount = accountService.save(newAccount);
			Customer newCustomer = new Customer();
		    newCustomer.setAccount(savedAccount);
		    newCustomer.setFullname(newAccount.getUsername()); 
		    System.out.println("Đối tượng Customer: " + newCustomer); 
		    newCustomer.setPhoneNumber(null);
		    customerRepository.save(newCustomer); 
		    model.addAttribute("message", "Đăng ký thành công!");
		    return "site/accounts/login"; 
		} catch (IllegalArgumentException e) {
		    model.addAttribute("errorMessage", e.getMessage());
		    return "site/accounts/register"; 
		} catch (Exception e) {
		    e.printStackTrace(); // In ra chi tiết ngoại lệ
		    model.addAttribute("errorMessage", "Có lỗi xảy ra khi tạo khách hàng!"+e.getMessage());
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
			return "error";
		}
		model.addAttribute("token", token);
		return "site/accounts/resetpassword";
	}

	@PostMapping("/resetpassword")
	public String resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword, RedirectAttributes redirectAttributes,
			Model model) {
		try {
			accountService.resetPassword(token, newPassword, confirmPassword);
			redirectAttributes.addFlashAttribute("message", "Đặt lại mật khẩu thành công!");
			return "redirect:/yourstyle/accounts/login";
		} catch (IllegalArgumentException e) {
			model.addAttribute("token", token);
			model.addAttribute("error", e.getMessage());
			return "site/accounts/resetpassword";
		}
	}

}
