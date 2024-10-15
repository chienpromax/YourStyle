package yourstyle.com.shope.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.service.AccountService;

@Controller
@RequestMapping("admin/accounts")
public class AccountController {
    @Autowired
    AccountService accountService;

    @RequestMapping("")
    public String list(ModelMap model) {
        List<Account> list = accountService.findAll();
        model.addAttribute("categories", list);
        return "admin/accounts/list";
    }

    @GetMapping("add")
    public String add(Model model) {
        List<Account> accounts = accountService.findAll();
        model.addAttribute("accounts", accounts);
        model.addAttribute("customer", new Customer());

        return "admin/customers/addOrEdit";
    }

}
