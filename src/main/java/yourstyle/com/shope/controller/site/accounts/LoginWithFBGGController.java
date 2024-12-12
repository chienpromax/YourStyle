package yourstyle.com.shope.controller.site.accounts;

import java.io.IOException;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Role;
import yourstyle.com.shope.service.AccountService;

import org.springframework.stereotype.Component;

import groovy.lang.Lazy;

@Component
public class LoginWithFBGGController extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    @Lazy
    private AccountService accountService;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    // Mảng ký tự để tạo mật khẩu ngẫu nhiên
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 15;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String username = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");

        Account account = accountService.findByUsernameOrEmail(username ,email);

        if (account == null) {
            // Tạo tài khoản mới nếu chưa tồn tại
            account = new Account();
            account.setUsername(username);
            account.setEmail(email);
            account.setPassword(bCryptPasswordEncoder.encode(generateRandomPassword()));

            Role roleUser = new Role();
            roleUser.setRoleId(1); // Role ID mặc định
            roleUser.setName("ROLE_USER");
            account.setRole(roleUser);

            accountService.save(account);

            Customer newCustomer = new Customer();
            newCustomer.setAccount(account);
            accountService.saveCustomer(newCustomer);
        }

        // Set thông tin tài khoản vào SecurityContextHolder
        CustomUserDetails userDetails = new CustomUserDetails(account);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        response.sendRedirect("/yourstyle/home");
    }

    // Phương thức tạo mật khẩu ngẫu nhiên
    private String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }
}
