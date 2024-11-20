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

    Page<Customer> findByFullnameContainingOrPhoneNumberContaining(String fullname, String phoneNumber,
            Pageable pageable);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT c FROM Customer c WHERE c.account.accountId = ?1")
    Customer findByCustomerAccountId(Integer accountId);

    @Query("SELECT c FROM Customer c WHERE c.account.accountId = ?1")
    Customer findByAccountId(Integer accountId);

    @Query("SELECT c FROM Customer c WHERE c.account.role.name = :role")
    Page<Customer> findAllByRole(@Param("role") String role, Pageable pageable);

    // @Query("SELECT c FROM Customer c WHERE (c.fullname LIKE %:value% OR
    // c.phoneNumber LIKE %:value%) AND c.account.role.name = 'ROLE_EMPLOYEE'")
    // Page<Customer> searchEmployeesByNameOrPhone(@Param("value") String value,
    // Pageable pageable);

    @Query("SELECT c FROM Customer c JOIN FETCH c.account a WHERE (c.fullname LIKE %:value% OR c.phoneNumber LIKE %:value%) AND a.role.name = 'ROLE_EMPLOYEE'")
    Page<Customer> searchEmployeesByNameOrPhone(@Param("value") String value, Pageable pageable);

}