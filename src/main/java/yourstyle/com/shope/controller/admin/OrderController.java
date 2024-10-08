package yourstyle.com.shope.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin/orders")
public class OrderController {
    @GetMapping("add")
    public String add() {
        return "admin/orders/addOrEdit";
    }

    @RequestMapping("")
    public String list() {
        return "admin/orders/list";
    }
}
