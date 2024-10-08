package yourstyle.com.shope.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin/staffs")
public class StaffController {
    @GetMapping("add")
    public String add() {
        return "admin/staffs/addOrEdit";
    }

    @RequestMapping("/")
    public String list() {
        return "admin/staffs/list";
    }
}
