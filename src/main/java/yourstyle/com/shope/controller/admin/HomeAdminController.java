package yourstyle.com.shope.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeAdminController {
    @RequestMapping({ "/", "/admin/home" })
    public String showAdminPage() {
        return "admin/pages/home";
    }
}
