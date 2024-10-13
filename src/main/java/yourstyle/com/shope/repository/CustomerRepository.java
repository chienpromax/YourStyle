package yourstyle.com.shope.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import yourstyle.com.shope.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	// @Query("SELECT cus FROM Customer ac WHERE cus.fullname LIKE %?1% OR
	// cus.phonenumber LIKE %?1%")
	// Page<Customer> findByFullnameOrPhone(String value, Pageable pageable);

	// Customer findByFullnameOrPhone(String fullname, String phone);
}
