package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository;

	public CustomerServiceImpl(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Override
	public Customer update(Customer customer) {
		return customerRepository.save(customer);
	}

	@Override
	public <S extends Customer> S save(S entity) {
		return customerRepository.save(entity);
	}

	@Override
	public <S extends Customer> Optional<S> findOne(Example<S> example) {
		return customerRepository.findOne(example);
	}

	@Override
	public Page<Customer> findAll(Pageable pageable) {
		return customerRepository.findAll(pageable);
	}

	@Override
	public List<Customer> findAll(Sort sort) {
		return customerRepository.findAll(sort);
	}

	@Override
	public Page<Customer> searchByNameOrPhone(String value, Pageable pageable) {
		return customerRepository.findByFullnameContainingOrPhoneNumberContaining(value, value, pageable);
	}

	@Override
	public List<Customer> findAll() {
		return customerRepository.findAll();
	}

	@Override
	public Optional<Customer> findById(Integer id) {
		return customerRepository.findById(id);
	}

	@Override
	public long count() {
		return customerRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		customerRepository.deleteById(id);
	}

	@Override
	public List<Customer> searchByNameOrPhone(String value) {
		return customerRepository.findByFullnameContainingOrPhoneNumberContaining(value, value);
	}

	@Override
	public Page<Customer> findAllNotRetailCustomer(Integer customerId, Pageable pageable) {
		return customerRepository.findAllNotRetailCustomer(customerId, pageable);
	}

	@Override
	public Page<Customer> findByFullnameContaining(Integer customerId, String fullname, Pageable pageable) {
		return customerRepository.findByFullnameContaining(customerId, fullname, pageable);
	}

	@Override
	public Page<Customer> findByPhoneName(Integer customerId, String phoneNumber, Pageable pageable) {
		return customerRepository.findByPhoneName(customerId, phoneNumber, pageable);
	}

	// @Override
	// public Page<Customer> findByFullnameOrPhoneNumber(String value, Pageable
	// pageable) {
	// return customerRepository.findByFullnameOrPhoneNumber(value, pageable);
	// }

	// @Override
	// public Customer findByFullnameOrPhoneNumber(String fullname, String
	// phoneNumber) {
	// return customerRepository.findByFullnameOrPhoneNumber(fullname, phoneNumber);
	// }

}