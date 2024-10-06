package yourstyle.com.shope.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin/vouchers")
public class VoucherController {
    @GetMapping("add")
    public String add() {
        return "admin/vouchers/addOrEdit";
    }

    @RequestMapping("")
    public String list() {
        return "admin/vouchers/list";
    }
}
