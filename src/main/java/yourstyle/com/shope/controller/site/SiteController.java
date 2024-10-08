package yourstyle.com.shope.controller.site;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SiteController {
    
    @RequestMapping("/login")
    public String showLoginPage() {
        return "site/pages/login"; // Đường dẫn đến tệp login.html
    }

    @RequestMapping("/register")
    public String showRegisterPage() {
        return "site/pages/register"; // Đường dẫn đến tệp register.html
    }

    @RequestMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "site/pages/forgotpassword"; // Đường dẫn đến tệp forgotpassword.html
    }

    @RequestMapping("/personal-information")
    public String showPersonalInformationPage() {
        return "site/pages/personalinformation"; // Đường dẫn đến tệp personalinformation.html
    }
}
