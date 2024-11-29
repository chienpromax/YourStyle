package yourstyle.com.shope.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.validation.admin.AddressDto;
import yourstyle.com.shope.model.Customer;


@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    @Query("SELECT a FROM Address a WHERE a.customer.customerId = ?1")
    List<Address> findByCustomerId(Integer customerId);

    @Query("SELECT a FROM Address a WHERE a.customer.customerId = ?1 AND a.isDefault = true")
    Optional<Address> findDefaultByCustomerId(Integer customerId);


    @Modifying
    @Transactional
    @Query("UPDATE Address a SET a.isDefault = :status WHERE a.customer.customerId = :customerId")
    void updateDefaultStatus(@Param("status") boolean status, @Param("customerId") Integer customerId);

    @Query("SELECT new yourstyle.com.shope.validation.admin.AddressDto(a.addressId,a.street, a.ward, a.district, a.city,a.customer,a.isDefault) FROM Address a WHERE a.customer.customerId = :customerId")
	List<AddressDto> findByAddressDtoCustomerID(@Param("customerId") Integer customerId);
    
    @Query("SELECT a FROM Address a WHERE a.customer.customerId = ?1")
	List<Address> findByAddressCustomerID(Integer customerId);

    @Transactional
    void deleteAllByCustomer(Customer customer);


}
