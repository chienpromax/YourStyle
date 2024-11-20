package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.repository.AddressRepository;
import yourstyle.com.shope.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    AddressRepository addressRepository;

      @Override
    public void updateDefaultAddress(Address address, Integer customerId) {
        // Hủy cờ isDefault của tất cả địa chỉ thuộc customerId
        addressRepository.updateDefaultStatus(false, customerId);

        // Cập nhật địa chỉ được chọn làm mặc định
        address.setIsDefault(true);
        addressRepository.save(address);
    }

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
}
