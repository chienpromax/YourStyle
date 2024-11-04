package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.repository.AddressRepository;
import yourstyle.com.shope.service.AddressService;
import yourstyle.com.shope.validation.admin.AddressDto;

@Service
public class AddressServiceImpl implements AddressService {
	@Autowired
	AddressRepository addressRepository;
	@Autowired
	private Converter<Address, AddressDto> addressToAddressDtoConverter;

	public AddressServiceImpl(AddressRepository addressRepository) {
		this.addressRepository = addressRepository;
	}

	@Override
	public List<Address> findByAddressCustomerID(Integer customerId) {
		return addressRepository.findByAddressCustomerID(customerId);
	}

	@Override
	public <S extends Address> S save(S entity) {
		return addressRepository.save(entity);
	}

	@Override
	public List<Address> findAll(Sort sort) {
		return addressRepository.findAll(sort);
	}

	@Override
	public Page<Address> findAll(Pageable pageable) {
		return addressRepository.findAll(pageable);
	}

	@Override
	public List<Address> findAll() {
		return addressRepository.findAll();
	}

	@Override
	public Optional<Address> findById(Integer id) {
		return addressRepository.findById(id);
	}

	@Override
	public boolean existsById(Integer id) {
		return addressRepository.existsById(id);
	}

	@Override
	public <S extends Address> boolean exists(Example<S> example) {
		return addressRepository.exists(example);
	}

	@Override
	public Address getOne(Integer id) {
		return addressRepository.getOne(id);
	}

	@Override
	public long count() {
		return addressRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		addressRepository.deleteById(id);
	}

	@Override
	public Address getById(Integer id) {
		return addressRepository.getById(id);
	}

	@Override
	public void delete(Address entity) {
		addressRepository.delete(entity);
	}

	@Override
	public List<AddressDto> findByAddressDtoCustomerID(Integer customerId) {
		List<Address> address = addressRepository.findByAddressCustomerID(customerId);
		return address.stream().map(addressToAddressDtoConverter::convert).collect(Collectors.toList());
	}

}
