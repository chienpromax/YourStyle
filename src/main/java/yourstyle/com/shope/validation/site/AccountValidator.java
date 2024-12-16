package yourstyle.com.shope.validation.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.repository.AccountRepository;

@Component
public class AccountValidator implements Validator {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountValidator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return Account.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        Account account = (Account) target;
    
        // Kiểm tra username
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty.account.username", "Họ và tên không được để trống");
        if (account.getUsername() != null) {
            if (account.getUsername().length() < 10) {
                errors.rejectValue("username", "Length.account.username", "Họ và tên phải có ít nhất 10 ký tự");
            }
            if (!account.getUsername().matches(".*[a-zA-Z].*")) {
                errors.rejectValue("username", "Pattern.account.username", "Họ và tên phải chứa ít nhất 1 chữ cái");
            }
            if (!account.getUsername().matches(".*[0-9].*")) {
                errors.rejectValue("username", "Pattern.account.username", "Họ và tên phải chứa ít nhất 1 chữ số");
            }
            if (!account.getUsername().matches(".*[-_!@#$%^&*(),.?\":{}|<>].*")) {
                errors.rejectValue("username", "Pattern.account.username", "Họ và tên phải chứa ít nhất 1 ký tự đặc biệt");
            }
            if (account.getUsername().contains(" ")) {
                errors.rejectValue("username", "Whitespace.account.username", "Họ và tên không được chứa khoảng trắng");
            }
            if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
                errors.rejectValue("username", "Duplicate.account.username", "Tên đăng nhập đã tồn tại");
            }
        }
    
        // Kiểm tra email
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.account.email", "Email không được để trống");
        if (account.getEmail() != null) {
            if (!account.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                errors.rejectValue("email", "Pattern.account.email", "Email không đúng định dạng");
            } else if (accountRepository.findByEmail(account.getEmail()).isPresent()) {
                errors.rejectValue("email", "Duplicate.account.email", "Email đã tồn tại");
            }
        }
    
        // Kiểm tra password
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.account.password", "Mật khẩu không được để trống");
    }

}
