package yourstyle.com.shope.controller.site;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "site/pages/login"; // Trả về trang login.html
    }
}
