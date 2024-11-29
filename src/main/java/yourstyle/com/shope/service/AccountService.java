package yourstyle.com.shope.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

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

	Page<Account> findByUsernameOrEmail(String name, Pageable pageable);

	Account findByUsernameOrEmail(String username, String email);

	Account findByUsername(String username);

	void register(Account account, String confirmPassword);

	Account login(String username, String password);

	boolean sendResetPasswordLink(String email);

	void resetPassword(String token, String newPassword, String confirmPassword);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	Optional<Account> findByEmail(String email);

	// new

	boolean updateEmailByUsername(String username, String newEmail);

	boolean isEmailExist(String email);

	boolean isValidEmail(String email);

	String getPhoneNumberByUsername(String username);

	boolean addOrUpdatePhoneNumber(String username, String newPhone);

	boolean updateUserInfo(String username, String fullName, LocalDate birthday, String gender, String nationality,
			MultipartFile avatar);

	String getNationalityByUsername(String username);

	Boolean getGenderByUsername(String username);

	Date getBirthdayByUsername(String username);

	String getFullNameByUsername(String username);

	String getAvatarByUsername(String username);

}
