package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;

import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.validation.admin.AddressDto;
import yourstyle.com.shope.model.Customer;


public interface AddressService {

    void save(Address address);

    List<Address> findByCustomerId(Integer customerId);

    Optional<Address> findDefaultByCustomerId(Integer customerId);

    void deleteById(Integer addressId);

    Optional<Address> findById(Integer addressId);

    void removeDefaultAddress(Integer customerId);
    void updateDefaultAddress(Address address, Integer customerId);;

    List<AddressDto> findByAddressDtoCustomerID(Integer customerId);
    
    List<Address> findByAddressCustomerID(Integer customerId);

    void deleteAllByCustomer(Customer customer);

    boolean existsById(Integer addressId);

}