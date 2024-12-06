package yourstyle.com.shope.controller.site.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpSession;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.service.AccountService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/yourstyle/accounts")
@Slf4j
public class LoginController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "locked", required = false) String locked,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng.");
        }
        if (locked != null) {
            model.addAttribute("errorMessage", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
        }
        return "site/accounts/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session,
            Model model) {
        try {
            Account account = accountService.login(username, password);
            session.setAttribute("loggedInUser", account);
            return "redirect:/yourstyle/home";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "site/accounts/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "yourstyle/accounts/login";
    }
}
