package yourstyle.com.shope.controller.site;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/yourstyle")
public class AboutController {
    @RequestMapping( "/about")
    public String showHomePage() {
        return "site/pages/about";
    }
}

