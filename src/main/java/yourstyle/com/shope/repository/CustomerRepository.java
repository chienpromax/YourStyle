package yourstyle.com.shope.repository;

import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

        List<Customer> findByFullnameContainingOrPhoneNumberContaining(String fullname, String phoneNumber);

        Page<Customer> findByFullnameContainingOrPhoneNumberContaining(String fullname, String phoneNumber,
                        Pageable pageable);

        boolean existsByPhoneNumber(String phoneNumber);

        @Query("SELECT c FROM Customer c WHERE c.account.accountId = ?1")
        Customer findByCustomerAccountIdWithQuery(Integer accountId);

        @Query("SELECT c FROM Customer c WHERE c.account.accountId = ?1")
        Customer findByAccountId(Integer accountId);

        @Query("SELECT c FROM Customer c WHERE c.account.role.name = :role")
        Page<Customer> findAllByRole(@Param("role") String role, Pageable pageable);

        @Query("SELECT c FROM Customer c JOIN FETCH c.account a WHERE (c.fullname LIKE %:value% OR c.phoneNumber LIKE %:value%) AND a.role.name = 'ROLE_EMPLOYEE'")
        Page<Customer> searchEmployeesByNameOrPhone(@Param("value") String value, Pageable pageable);

        @Query("SELECT c FROM Customer c WHERE NOT c.customerId = :customerId AND c.fullname LIKE CONCAT('%',:fullname ,'%')")
        Page<Customer> findByFullnameContaining(
                        @Param("customerId") Integer customerId,
                        @Param("fullname") String fullname,
                        Pageable pageable);

        @Query("SELECT c FROM Customer c WHERE NOT c.customerId = :customerId AND c.phoneNumber LIKE CONCAT('%',:phoneNumber,'%')")
        Page<Customer> findByPhoneName(
                        @Param("customerId") Integer customerId,
                        @Param("phoneNumber") String phoneNumber,
                        Pageable pageable);

        @Query("SELECT c FROM Customer c WHERE NOT c.customerId = :customerId")
        Page<Customer> findAllNotRetailCustomer(@Param("customerId") Integer customerId, Pageable pageable);

        // Customer findByCustomerAccountId(Integer accountId);

        Customer findByAccount_AccountId(Integer accountId);

        @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.addresses WHERE c.customerId = :customerId")
        Optional<Customer> findCustomerWithAddresses(@Param("customerId") Integer customerId);

        @Query("SELECT c FROM Customer c WHERE c.account.accountId = ?1")
        Customer findByCustomerAccountId(Integer accountId);

        @Query("SELECT COUNT(c) FROM Customer c WHERE DATE(c.createDate) = CURRENT_DATE")
        Long countByCreateDateToday();

        @Query("SELECT COUNT(c) FROM Customer c WHERE MONTH(c.createDate) = MONTH(CURRENT_DATE) AND YEAR(c.createDate) = YEAR(CURRENT_DATE)")
        Long countByCreateDateThisMonth();

        @Query("SELECT COUNT(c) FROM Customer c WHERE YEAR(c.createDate) = YEAR(CURRENT_DATE)")
        Long countByCreateDateThisYear();

        Customer findByAccount(Account account);

        Optional<Customer> findOptionalByAccount_AccountId(Integer accountId);

}