package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.validation.admin.AddressDto;

public interface AddressService {

	void delete(Address entity);

	Address getById(Integer id);

	void deleteById(Integer id);

	long count();

	Address getOne(Integer id);

	<S extends Address> boolean exists(Example<S> example);

	boolean existsById(Integer id);

	Optional<Address> findById(Integer id);

	List<Address> findAll();

	Page<Address> findAll(Pageable pageable);

	List<Address> findAll(Sort sort);

	<S extends Address> S save(S entity);

	List<Address> findByAddressCustomerID(Integer customerId);

	List<AddressDto> findByAddressDtoCustomerID(Integer customerId);
}
