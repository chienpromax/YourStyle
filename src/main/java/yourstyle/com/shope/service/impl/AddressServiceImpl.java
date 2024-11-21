package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.repository.AddressRepository;
import yourstyle.com.shope.service.AddressService;
import yourstyle.com.shope.validation.admin.AddressDto;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    AddressRepository addressRepository;
    @Autowired
	private Converter<Address, AddressDto> addressToAddressDtoConverter;

    @Override
    public void save(Address address) {
        addressRepository.save(address);
    }

    @Override
    public List<Address> findByCustomerId(Integer customerId) {
        return addressRepository.findByCustomerId(customerId);
    }

    @Override
    public Optional<Address> findDefaultByCustomerId(Integer customerId) {
        return addressRepository.findDefaultByCustomerId(customerId);
    }

    @Override
    public void deleteById(Integer addressId) {
        addressRepository.deleteById(addressId);
    }

    @Override
    public Optional<Address> findById(Integer addressId) {
        return addressRepository.findById(addressId);
    }

    @Override
    public void removeDefaultAddress(Integer customerId) {
        List<Address> addresses = findByCustomerId(customerId);
        for (Address address : addresses) {
            if (address.getIsDefault()) {
                address.setIsDefault(false);
                save(address);
            }
        }
    }

    @Override
	public List<AddressDto> findByAddressDtoCustomerID(Integer customerId) {
		List<Address> address = addressRepository.findByAddressCustomerID(customerId);
		return address.stream().map(addressToAddressDtoConverter::convert).collect(Collectors.toList());
	}

    @Override
	public List<Address> findByAddressCustomerID(Integer customerId) {
		return addressRepository.findByAddressCustomerID(customerId);
	}
    
    @Override
    public void deleteAllByCustomer(Customer customer) {
        addressRepository.deleteAllByCustomer(customer);
    }

    @Override
    public boolean existsById(Integer addressId) {
        return addressRepository.existsById(addressId);
    }
    
}
