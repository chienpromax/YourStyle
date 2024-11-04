package yourstyle.com.shope.validation.site;

import yourstyle.com.shope.model.Account;

public class AccountValidation {

	public static void validate(Account account) {
		if (account.getUsername() == null || account.getUsername().isEmpty()) {
			throw new IllegalArgumentException("Tên đăng nhập không được để trống!");
		}

		if (account.getEmail() == null || !account.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
			throw new IllegalArgumentException("Email không hợp lệ!");
		}

		if (account.getPassword() == null || account.getPassword().length() < 6) {
			throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự!");
		}
	}
}
