package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import jakarta.servlet.http.HttpSession;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Customer;

public interface AccountService {

    void deleteById(Integer id);

    long count();

    Optional<Account> findById(Integer id);

    List<Account> findAll();

    Page<Account> findAll(Pageable pageable);

    List<Account> findAll(Sort sort);

    <S extends Account> Optional<S> findOne(Example<S> example);

    <S extends Account> S save(S entity);

    Page<Account> findByUsernameOrEmail(String name, Pageable pageable);

    Account findByUsernameOrEmail(String username, String email);

    Account findByUsername(String username);

    void register(Account account, String confirmPassword);

    Account login(String username, String password);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<Account> findByEmail(String email);

    List<Account> findAccountsWithoutCustomer();

    Customer saveCustomer(Customer customer);

    String sendOTP(String email, HttpSession session);  // Gửi OTP

    boolean verifyOTP(String email, String otp);  // Kiểm tra OTP

    void storeOTP(String email, String otp);  // Lưu OTP vào cơ sở dữ liệu

    void resetPasswordByEmail(String email, String newPassword);// Đặt lại mật khẩu bằng OTP

    boolean verifyOTPForPasswordReset(String otp);  // Xác minh OTP để reset mật khẩu

    String sendOTPForPasswordReset(String email);  // Gửi OTP để reset mật khẩu
}
