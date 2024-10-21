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
import yourstyle.com.shope.model.Role;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.repository.AccountRepository;
import yourstyle.com.shope.repository.RoleRepository;
import yourstyle.com.shope.service.AccountService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private RoleRepository roleRepository;

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
		accountRepository.deleteById(id);
	}

	@Override
	public Page<Account> findByUsernameOrEmail(String value, Pageable pageable) {
		return accountRepository.findByUsernameOrEmail(value, pageable);
	}
	    
	    @Override
	    public Account findByUsernameOrEmail(String username, String email) {
	        return accountRepository.findByUsernameOrEmail(username, email);
	    }

	    public void register(Account account, String confirmPassword) {
	        String password = account.getPassword().trim();
	        confirmPassword = confirmPassword.trim();

	        // Kiểm tra mật khẩu
	        if (!password.equals(confirmPassword)) {
	            throw new IllegalArgumentException("Mật khẩu không khớp!");
	        }

	        // Tìm và thiết lập đối tượng Role với roleId = 1
	        Role role = roleRepository.findById(1)
	            .orElseThrow(() -> new IllegalArgumentException("Vai trò không tồn tại!"));
	        account.setRole(role); // Gán vai trò vào tài khoản

	        if (accountRepository.findByEmail(account.getEmail()).isPresent()) {
	            throw new IllegalArgumentException("Email đã tồn tại!");
	        }

	        // Kiểm tra tên đăng nhập
	        if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
	            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại!");
	        }

	        try {
	            account.setPassword(password); // Không mã hóa mật khẩu
	            accountRepository.save(account); // Lưu tài khoản vào cơ sở dữ liệu
	        } catch (DataIntegrityViolationException e) {
	            throw new RuntimeException("Có lỗi xảy ra khi tạo tài khoản: " + e.getMessage());
	        }
	    }

	    @Override
	    public Account login(String username, String password) {
	        Account account = accountRepository.findByUsername(username)
	                .orElseThrow(() -> new IllegalArgumentException("Tên đăng nhập không tồn tại!"));
	        if (!account.getPassword().equals(password)) {
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

	            // Lưu token vào tài khoản
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
	        // Kiểm tra nếu token rỗng hoặc null
	        if (token == null || token.isEmpty()) {
	            throw new IllegalArgumentException("Token không hợp lệ!");
	        }
	        Optional<Account> accountOptional = accountRepository.findByToken(token);
	        if (accountOptional.isPresent()) {
	            Account account = accountOptional.get();

	            if (!newPassword.equals(confirmPassword)) {
	                throw new IllegalArgumentException("Mật khẩu mới và mật khẩu xác nhận không khớp!");
	            }
	            account.setPassword(newPassword); // Không mã hóa mật khẩu

	            account.setToken(null);

	            accountRepository.save(account);
	        } else {
	            throw new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn!");
	        }
	    }
	}