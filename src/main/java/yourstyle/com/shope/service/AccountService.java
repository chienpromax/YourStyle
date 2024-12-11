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

	boolean sendResetPasswordLink(String email);

	void resetPassword(String token, String newPassword, String confirmPassword);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	Optional<Account> findByEmail(String email);

	List<Account> findAccountsWithoutCustomer();

	Customer saveCustomer(Customer customer);

}
