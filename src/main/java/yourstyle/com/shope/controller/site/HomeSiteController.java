package yourstyle.com.shope.controller.site;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Sửa import để sử dụng đúng Model
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeSiteController {

    // Đường dẫn cho trang home
    @RequestMapping({ "/", "/home" })
    public String showHomePage(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        model.addAttribute("userName", userName); // Thêm tên người dùng vào mô hình
        return "site/pages/home"; // Trả về view home
    }
}
