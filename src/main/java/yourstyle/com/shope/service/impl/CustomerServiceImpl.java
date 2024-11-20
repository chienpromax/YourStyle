package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.validation.admin.CustomerDto;
import yourstyle.com.shope.validation.admin.StaffDto;

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
	public boolean existsByPhoneNumber(String phoneNumber) {
		return customerRepository.existsByPhoneNumber(phoneNumber);
	}

	@Override
	public Customer findByAccountId(Integer accountId) {
		return customerRepository.findByAccountId(accountId);
	}

	@Override
	public Page<Customer> searchByNameOrPhoneStaff(String value, Pageable pageable) {
		return customerRepository.searchEmployeesByNameOrPhone(value, pageable);
	}

	@Override
	public Page<Customer> findAllStaff(String role, Pageable pageable) {
		return customerRepository.findAllByRole(role, pageable);
	}

	@Override
	public Page<StaffDto> getAllEmployees(Pageable pageable) {
		// Lấy các Customer với role là "ROLE_EMPLOYEE" từ Repository
		Page<Customer> customers = customerRepository.findAllByRole("ROLE_EMPLOYEE", pageable);
		return customers.map(this::convertToStaffDto);
	}

	private StaffDto convertToStaffDto(Customer customer) {
		return new StaffDto(
				customer.getCustomerId(),
				customer.getFullname(),
				customer.getAccount().getEmail(),
				customer.getPhoneNumber(),
				customer.getAccount().getRole().getName(),
				customer.getAccount().getStatus(),
				customer.getAvatar(),
				customer.getGender(),
				customer.getBirthday());
	}

	

}
