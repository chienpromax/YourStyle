package yourstyle.com.shope.repository;

import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import yourstyle.com.shope.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    List<Customer> findByFullnameContainingOrPhoneNumberContaining(String fullname, String phoneNumber);

    Page<Customer> findByFullnameContainingOrPhoneNumberContaining(String fullname, String phoneNumber,
            Pageable pageable);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT c FROM Customer c WHERE c.account.accountId = ?1")
    Customer findByCustomerAccountId(Integer accountId);

    @Query("SELECT COUNT(c) FROM Customer c WHERE DATE(c.createDate) = CURRENT_DATE")
    Long countByCreateDateToday();

    @Query("SELECT COUNT(c) FROM Customer c WHERE MONTH(c.createDate) = MONTH(CURRENT_DATE) AND YEAR(c.createDate) = YEAR(CURRENT_DATE)")
    Long countByCreateDateThisMonth();

    @Query("SELECT COUNT(c) FROM Customer c WHERE YEAR(c.createDate) = YEAR(CURRENT_DATE)")
    Long countByCreateDateThisYear();
}