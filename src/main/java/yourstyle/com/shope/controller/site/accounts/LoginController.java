package yourstyle.com.shope.controller.site.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpSession;
import yourstyle.com.shope.service.AccountService;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/yourstyle/accounts")
@Slf4j
public class LoginController {
	@Autowired
	private AccountService accountService;

	@GetMapping("/login")
	public String loginPage() {
		return "site/accounts/login";
	}

	// @PostMapping("/login")
	// public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
	//     try {
	//         Account account = accountService.login(username, password);
	//         return "redirect:/yourstyle/home"; 
	//     } catch (IllegalArgumentException e) {
	//         model.addAttribute("errorMessage", e.getMessage());
	//         return "site/accounts/login"; 
	//     }
	// }


	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate(); // Xóa session
		return "yourstyle/accounts/login";
	}
}
