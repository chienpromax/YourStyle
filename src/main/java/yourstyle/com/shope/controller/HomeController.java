package yourstyle.com.shope.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping({ "/", "/admin/index" })
    public String home() {
        return "admin/index";
    }
}
