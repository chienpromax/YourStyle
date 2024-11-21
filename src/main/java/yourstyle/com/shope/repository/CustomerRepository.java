package yourstyle.com.shope.repository;

import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

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
    Customer findByCustomerAccountIdWithQuery(Integer accountId);

    @Query("SELECT c FROM Customer c WHERE c.account.accountId = ?1")
    Customer findByAccountId(Integer accountId);

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

//     Customer  findByCustomerAccountId(Integer accountId);

    Customer findByAccount_AccountId(Integer accountId);

    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.addresses WHERE c.customerId = :customerId")
    Optional<Customer> findCustomerWithAddresses(@Param("customerId") Integer customerId);

}