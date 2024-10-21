package yourstyle.com.shope.controller.site;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("userSecurityController")
public class SecurityController {
    @RequestMapping("/security/login/form")
    public String loginForm(Model model) {
        model.addAttribute("message", "Login please!");
        return "site/accounts/login";
    }

    @RequestMapping("/security/login/success")
    public String loginSuccess(Model model) {
        model.addAttribute("message", "Login success!");
        return "site/accounts/login";
    }

    @RequestMapping("/security/login/error")
    public String loginError(Model model) {
        model.addAttribute("message", "User invalid!");
        return "site/accounts/login";
    }

    @RequestMapping("/security/unauthorized")
    public String unauthoried(Model model) {
        model.addAttribute("message", "you do not have access!");
        return "site/accounts/login";
    }

    @RequestMapping("/security/logout/success")
    public String logoffSuccess(Model model) {
        model.addAttribute("message", "Logout succsess!");
        return "site/accounts/login";
    }
}
