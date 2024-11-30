package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;

import yourstyle.com.shope.model.Customer;

public interface CustomerService {

	void deleteById(Integer id);

	long count();

	Optional<Customer> findById(Integer id);

	List<Customer> findAll();

	Page<Customer> findAll(Pageable pageable);

	Page<Customer> searchByNameOrPhone(String value, Pageable pageable);

	List<Customer> findAll(Sort sort);

	<S extends Customer> Optional<S> findOne(Example<S> example);

	<S extends Customer> S save(S entity);

	Customer update(Customer customer);

	List<Customer> searchByNameOrPhone(String value);

	boolean existsByPhoneNumber(String phoneNumber);

	Customer findByAccountId(Integer accountId);

	Page<Customer> findByFullnameContaining(Integer customerId, String fullname, Pageable pageable);

	Page<Customer> findByPhoneName(Integer customerId, String phoneNumber, Pageable pageable);

	Page<Customer> findAllNotRetailCustomer(Integer customerId, Pageable pageable);

	// Thêm phương thức này
	String getEmailByUsername(String username);

	Integer getAccountIdByUsername(String username);

}
