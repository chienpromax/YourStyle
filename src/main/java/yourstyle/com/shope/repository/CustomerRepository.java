package yourstyle.com.shope.repository;

import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import yourstyle.com.shope.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    List<Customer> findByFullnameContainingOrPhoneNumberContaining(String fullname, String phoneNumber);

    Page<Customer> findByFullnameContainingOrPhoneNumberContaining(String fullname, String phoneNumber, Pageable pageable);
}


// @Query("SELECT ct FROM Customer ct WHERE ct.fullname LIKE CONCAT('%', :value,'%') OR ct.phoneNumber LIKE CONCAT('%', :value, '%')")
// Page<Customer> findByFullnameOrPhoneNumber(String value, Pageable pageable);

// Customer findByFullnameOrPhoneNumber(String fullname, String phoneNumber);