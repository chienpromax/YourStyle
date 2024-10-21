package yourstyle.com.shope.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.service.AccountService;

@Controller("loginController")
@RequestMapping("/yourstyle/accounts")
public class LoginController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/login") // Thêm phương thức GET
    public String loginPage() {
        return "site/accounts/login"; // Trả về trang đăng nhập
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model,
            HttpSession session) {
        try {
            Account account = accountService.login(username, password); // Sử dụng đối tượng Account
            session.setAttribute("userName", account.getUsername()); // Lưu tên người dùng vào session
            return "redirect:/home"; // Điều hướng tới trang chính sau khi đăng nhập thành công
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "site/accounts/login"; // Quay lại trang đăng nhập nếu thất bại
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Xóa session
        return "redirect:/yourstyle/accounts/login"; // Điều hướng về trang đăng nhập
    }

}
