package yourstyle.com.shope.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin/accounts")
public class AccountController {
    @GetMapping("add")
    public String add() {
        return "admin/accounts/addOrEdit";
    }

    @RequestMapping("/")
    public String list() {
        return "admin/accounts/list";
    }
}
