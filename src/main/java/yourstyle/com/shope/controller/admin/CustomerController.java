package yourstyle.com.shope.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin/customers")
public class CustomerController {
    @GetMapping("add")
    public String add() {
        return "admin/customers/addOrEdit";
    }

    @RequestMapping("/")
    public String list() {
        return "admin/customers/list";
    }
}
