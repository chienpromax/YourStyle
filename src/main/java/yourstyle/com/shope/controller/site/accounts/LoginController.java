package yourstyle.com.shope.controller.site.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.service.AccountService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/yourstyle/accounts")
public class LoginController {

    @Autowired
    AccountService accountService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Tên người dùng hoặc mật khẩu không đúng. Vui lòng thử lại.");
        }
        return "site/accounts/login";
    }

    @PostMapping("/yourstyle/accounts/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        Account account = accountService.findByUsername(username);

        if (account == null) {
            model.addAttribute("errorMessage", "Tên người dùng không tồn tại.");
            return "site/accounts/login";
        }
        boolean isPasswordMatch = bCryptPasswordEncoder.matches(password, account.getPassword());

        if (isPasswordMatch) {
            return "redirect:/yourstyle/home";
        } else {
            model.addAttribute("errorMessage", "Tên người dùng hoặc mật khẩu không đúng.");
            return "site/accounts/login";
        }
    }

    @GetMapping("/logout")
    public String logoutPage(Model model) {
        model.addAttribute("message", "Bạn đã đăng xuất thành công.");
        return "site/accounts/login";
    }
}
