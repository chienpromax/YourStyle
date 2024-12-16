package yourstyle.com.shope.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import yourstyle.com.shope.model.Role;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.OTPToken;
import yourstyle.com.shope.repository.AccountRepository;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.repository.OTPRepository;
import yourstyle.com.shope.repository.RoleRepository;
import yourstyle.com.shope.repository.SearchHistoryRepository;
import yourstyle.com.shope.service.AccountService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import yourstyle.com.shope.service.EmailService;

import java.util.stream.Collectors;


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
    // new
    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private EmailService emailService;

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

        if (!account.getStatus()) {
            throw new IllegalArgumentException("Tài khoản của bạn đã bị khóa!");
        }
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu không đúng!");
        }
        return account;
    }


    @Override
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Tên đăng nhập không tồn tại!"));
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public List<Account> findAccountsWithoutCustomer() {
        // Giả sử bạn có một repository để truy vấn
        List<Account> allAccounts = accountRepository.findAll();
        List<Integer> customerAccountIds = customerRepository.findAll()
                .stream()
                .map(customer -> customer.getAccount().getAccountId())
                .collect(Collectors.toList());

        return allAccounts.stream()
                .filter(account -> !customerAccountIds.contains(account.getAccountId()))
                .collect(Collectors.toList());
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // new
    public String sendOTP(String email, HttpSession session) {
        String otp = generateNumericOTP(6);  // Tạo OTP 6 ký tự số

        try {
            // Gửi OTP qua email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Mã xác nhận OTP");
            helper.setText("Mã OTP của bạn là: " + otp);
            mailSender.send(message);

            // Lưu OTP vào session để kiểm tra sau
            session.setAttribute("otp", otp);
            storeOTP(email, otp); // Lưu OTP vào cơ sở dữ liệu hoặc nơi cần thiết

        } catch (Exception e) {
            e.printStackTrace();
        }

        return otp;
    }

    // Hàm tạo OTP số ngẫu nhiên
    private String generateNumericOTP(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));  // Lấy ngẫu nhiên 1 chữ số từ 0 đến 9
        }

        return otp.toString();
    }


    public void storeOTP(String email, String otp) {
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);
        otpRepository.save(new OTPToken(email, otp, expiryTime));
    }

    public boolean verifyOTP(String email, String otp) {
        Optional<OTPToken> otpTokenOptional = otpRepository.findByEmailAndOtp(email, otp);

        if (otpTokenOptional.isPresent()) {
            OTPToken otpToken = otpTokenOptional.get();
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(otpToken.getExpiryTime())) {
                return true;
            } else {
                throw new IllegalArgumentException("Mã OTP đã hết hạn!");
            }
        }
        throw new IllegalArgumentException("Mã OTP không hợp lệ!");
    }

    //CHANGE PASS
    // Tạo mã OTP ngẫu nhiên
    public String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);  // Tạo OTP 6 chữ số
        return String.valueOf(otp);
    }

    // Gửi OTP để đặt lại mật khẩu
    @Transactional
    public String sendOTPForPasswordReset(String email) {
        // Kiểm tra xem email có tồn tại trong hệ thống không
        Optional<Account> accountOptional = accountRepository.findByEmail(email);
        if (accountOptional.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy tài khoản với email này.");
        }
        Account account = accountOptional.get();
        String otp = generateOTP();  // Tạo OTP ngẫu nhiên
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);


        OTPToken otpToken = new OTPToken();
        otpToken.setEmail(email);
        otpToken.setOtp(otp);
        otpToken.setExpiryTime(expiryTime);
        otpToken.setAccount(account);
        otpRepository.save(otpToken);

        // Gửi OTP qua email
        try {
            emailService.sendEmail(email, "Mã OTP để đặt lại mật khẩu", "Mã OTP của bạn là: " + otp);
        } catch (Exception e) {
            throw new RuntimeException("Có lỗi khi gửi OTP qua email: " + e.getMessage());
        }

        return otp;
    }

    public boolean verifyOTPForPasswordReset(String otp) {
        Optional<OTPToken> otpTokenOptional = otpRepository.findByOtp(otp);
        if (otpTokenOptional.isPresent()) {
            OTPToken otpToken = otpTokenOptional.get();

            // Kiểm tra OTP có hết hạn không
            if (otpToken.getExpiryTime().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Mã OTP đã hết hạn!");
            }

            return true;
        } else {
            throw new IllegalArgumentException("Mã OTP không hợp lệ!");
        }
    }

    public void resetPasswordByEmail(String email, String newPassword) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại"));
        String encodedPassword = passwordEncoder.encode(newPassword);
        account.setPassword(encodedPassword);
        accountRepository.save(account);
    }


}


