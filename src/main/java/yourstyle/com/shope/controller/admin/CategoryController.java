package yourstyle.com.shope.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin/categorys/")
public class CategoryController {
    @GetMapping("add")
    public String add() {
        return "admin/categorys/addOrEdit";
    }

    @RequestMapping("/")
    public String list() {
        return "admin/categorys/list";
    }
}
