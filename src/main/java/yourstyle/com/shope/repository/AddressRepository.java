package yourstyle.com.shope.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.validation.admin.AddressDto;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
	@Query("SELECT a FROM Address a WHERE a.customer.customerId = ?1")
	List<Address> findByAddressCustomerID(Integer customerId);

	@Query("SELECT a FROM Address a WHERE a.customer.customerId = ?1")
	List<AddressDto> findByAddressDtoCustomerID(Integer customerId);
}
