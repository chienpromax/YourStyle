package yourstyle.com.shope.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.Customer;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    @Query("SELECT a FROM Address a WHERE a.customer.customerId = ?1")
    List<Address> findByCustomerId(Integer customerId);

    @Query("SELECT a FROM Address a WHERE a.customer.customerId = ?1 AND a.isDefault = true")
    Optional<Address> findDefaultByCustomerId(Integer customerId);

    @Transactional
    void deleteAllByCustomer(Customer customer);
}
