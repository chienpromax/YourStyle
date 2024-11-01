package yourstyle.com.shope.controller.site.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import yourstyle.com.shope.service.AccountService;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/yourstyle/accounts")
public class LoginController {

    @Autowired
    AccountService accountService;

    @GetMapping("/login")
    public String loginPage() {
        return "site/accounts/login";
    }

    @GetMapping("/logout")
    public String logoutPage() {
        return "site/accounts/login";
    }
}
