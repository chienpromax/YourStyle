package yourstyle.com.shope.validation.site;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import yourstyle.com.shope.model.Account;

@Component
public class AccountValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Account.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Account account = (Account) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty.account.username", "Họ và tên không được để trống");

        if (account.getUsername() != null) {
            if (!account.getUsername().matches("^[a-zA-Z]+$")) {
                errors.rejectValue("username", "Pattern.account.username", "Họ và tên không được có ký tự đặc biệt hoặc số");
            }
            if (account.getUsername().contains(" ")) {
                errors.rejectValue("username", "Whitespace.account.username", "Họ và tên không được chứa khoảng trắng");
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.account.email", "Email không được để trống");
        if (account.getEmail() != null && !account.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.rejectValue("email", "Pattern.account.email", "Email không đúng định dạng");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.account.password", "Mật khẩu không được để trống");
    }
}
