package yourstyle.com.shope.controller.site;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeSiteController {
    @RequestMapping({ "/", "/home" })
    public String showAdminPage() {
        return "site/pages/home";
    }
}
