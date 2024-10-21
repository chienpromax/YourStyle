//package yourstyle.com.shope.controller.site;
//
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import yourstyle.com.shope.model.Account; // Giả sử bạn đã có lớp Account
//import yourstyle.com.shope.service.AccountService; // Dịch vụ cho bảng account
//
//@Controller
//@RequestMapping("/yourstyle/accounts/login")
//public class OAuth2LoginController {
//
//    private final AccountService accountService; // Service để lưu thông tin tài khoản
//
//    public OAuth2LoginController(AccountService accountService) {
//        this.accountService = accountService; // Khởi tạo dịch vụ
//    }
//
//    @GetMapping("/oauth2/callback/github")
//    public String handleGitHubCallback(OAuth2AuthenticationToken authentication) {
//        // Lấy thông tin người dùng từ GitHub
//        String email = authentication.getPrincipal().getAttribute("email");
//        String provider = "github"; // Hoặc tùy chỉnh theo nhu cầu
//
//        // Kiểm tra xem tài khoản đã tồn tại chưa
//        Account existingAccount = accountService.findByEmail(email);
//        if (existingAccount == null) {
//            // Tạo mới tài khoản nếu chưa tồn tại
//            Account account = new Account();
//            account.setEmail(email);
//            account.setProvider(provider);
//            // Cài đặt thêm các thông tin khác cho tài khoản nếu cần
//            accountService.save(account); // Lưu tài khoản vào cơ sở dữ liệu
//        } else {
//            // Nếu tài khoản đã tồn tại, có thể cập nhật thông tin nếu cần
//            existingAccount.setProvider(provider);
//            accountService.save(existingAccount);
//        }
//
//        return "redirect:/yourstyle/home"; // Chuyển hướng đến trang chính sau khi đăng nhập thành công
//    }
//}
