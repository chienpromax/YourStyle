package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import yourstyle.com.shope.model.Role;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.repository.AccountRepository;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.repository.RoleRepository;
import yourstyle.com.shope.repository.SearchHistoryRepository;
import yourstyle.com.shope.service.AccountService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder; // Cần có @Autowired ở đây

	@Autowired
	private SearchHistoryRepository searchHistoryRepository;
	public AccountServiceImpl() {
	}

	public AccountServiceImpl(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public <S extends Account> S save(S entity) {
		return accountRepository.save(entity);
	}

	@Override
	public <S extends Account> Optional<S> findOne(Example<S> example) {
		return accountRepository.findOne(example);
	}

	@Override
	public List<Account> findAll(Sort sort) {
		return accountRepository.findAll(sort);
	}

	@Override
	public Page<Account> findAll(Pageable pageable) {
		return accountRepository.findAll(pageable);
	}

	@Override
	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	@Override
	public Optional<Account> findById(Integer id) {
		return accountRepository.findById(id);
	}

	@Override
	public long count() {
		return accountRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		searchHistoryRepository.deleteById(id); // Xóa các liên kết trước
		accountRepository.deleteById(id);
	}

	@Override
	public Page<Account> findByUsernameOrEmail(String value, Pageable pageable) {
		return accountRepository.findByUsernameOrEmail(value, pageable);
	}

	@Override
	public boolean existsByEmail(String email) {
		return accountRepository.existsByEmail(email);
	}

	@Override
	public boolean existsByUsername(String username) {
		return accountRepository.existsByUsername(username);
	}

	@Override
	public Account findByUsernameOrEmail(String username, String email) {
		return accountRepository.findByUsernameOrEmail(username, email);
	}

	@Override
	public void register(Account account, String confirmPassword) {
		System.out.println("dto : " + account.toString());
		String password = account.getPassword().trim();
		confirmPassword = confirmPassword.trim();

		if (!password.equals(confirmPassword)) {
			throw new IllegalArgumentException("Mật khẩu không khớp!");
		}

		Role role = roleRepository.findById(1)
				.orElseThrow(() -> new IllegalArgumentException("Vai trò không tồn tại!"));
		account.setRole(role);

		if (accountRepository.findByEmail(account.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email đã tồn tại!");
		}
		
		if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Tên đăng nhập đã tồn tại!");
		}

		try {
			
			account.setPassword(passwordEncoder.encode(password)); // Mã hóa mật khẩu trước khi lưu
			Account savedAccount = accountRepository.save(account);
			Customer newCustomer = new Customer();
			newCustomer.setAccount(savedAccount);
			customerRepository.save(newCustomer);
		} catch (DataIntegrityViolationException e) {
			throw new RuntimeException("Có lỗi xảy ra khi tạo tài khoản: " + e.getMessage());
		}
	}
	@Override
	public Account login(String username, String password) {
		Account account = accountRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Tên đăng nhập không tồn tại!"));

	if (!passwordEncoder.matches(password, account.getPassword())) {
			throw new IllegalArgumentException("Mật khẩu không đúng!");
		}
		return account;
	}

	


	
	@Override
	public boolean sendResetPasswordLink(String email) {
		Optional<Account> accountOptional = accountRepository.findByEmail(email);
		if (accountOptional.isPresent()) {
			Account account = accountOptional.get();
			String token = UUID.randomUUID().toString(); // Tạo token ngẫu nhiên

			account.setToken(token);
			accountRepository.save(account);

			sendEmailWithToken(email, token);
			return true;
		}
		return false;
	}

	private void sendEmailWithToken(String email, String token) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Liên kết đặt lại mật khẩu");
		message.setText("Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng nhấn vào liên kết sau để đặt lại mật khẩu: "
				+ "http://localhost:8080/yourstyle/accounts/resetpassword?token=" + token);
		mailSender.send(message);
	}

	@Override
	public void resetPassword(String token, String newPassword, String confirmPassword) {
		
		System.out.println("new : " + newPassword);
		System.out.println("confirm : "+confirmPassword);
		
		
	    if (token == null || token.isEmpty()) {
	        throw new IllegalArgumentException("Token không hợp lệ!");
	    }
	    
	    Optional<Account> accountOptional = accountRepository.findByToken(token);
	    if (accountOptional.isPresent()) {
	        Account account = accountOptional.get();
	        if (!newPassword.equals(confirmPassword)) {
	            throw new IllegalArgumentException("Mật khẩu mới và mật khẩu xác nhận không khớp!");
	        }
	        account.setPassword(passwordEncoder.encode(newPassword)); 
	        account.setToken(null); 
	        accountRepository.save(account); 
	    } else {
	        throw new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn!");
	    }
	}


	@Override
	public Account findByUsername(String username) {
		return accountRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Tên đăng nhập không tồn tại!"));
	}
}