package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import yourstyle.com.shope.model.Account;

public interface AccountService {

	void deleteById(Integer id);

	long count();

	Optional<Account> findById(Integer id);

	List<Account> findAll();

	Page<Account> findAll(Pageable pageable);

	List<Account> findAll(Sort sort);

	<S extends Account> Optional<S> findOne(Example<S> example);

	<S extends Account> S save(S entity);
	
	Page<Account> findByUsernameOrEmail(String name,Pageable pageable);
	
	Account findByUsernameOrEmail(String username,String email);

	// Phương thức đăng ký tài khoản
    void register(Account account, String confirmPassword);
    
    // Phương thức đăng nhập
    Account login(String username, String password);
    
    // Phương thức gửi liên kết đặt lại mật khẩu qua email
    boolean sendResetPasswordLink(String email);
    
    // Phương thức đặt lại mật khẩu bằng token, mật khẩu mới và mật khẩu xác nhận
    void resetPassword(String token, String newPassword, String confirmPassword);
}
