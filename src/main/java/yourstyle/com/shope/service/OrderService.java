package yourstyle.com.shope.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderStatus;

public interface OrderService {

	<S extends Order> List<S> findAll(Example<S> example, Sort sort);

	<S extends Order> List<S> findAll(Example<S> example);

	Order getById(Integer id);

	void deleteById(Integer id);

	Order getOne(Integer id);

	boolean existsById(Integer id);

	Optional<Order> findById(Integer id);

	<S extends Order> Page<S> findAll(Example<S> example, Pageable pageable);

	List<Order> findAllById(Iterable<Integer> ids);

	List<Order> findAll();

	Page<Order> findAll(Pageable pageable);

	List<Order> findAll(Sort sort);

	<S extends Order> Optional<S> findOne(Example<S> example);

	<S extends Order> List<S> saveAll(Iterable<S> entities);

	<S extends Order> S save(S entity);

	Page<Order> findByOrderId(Integer orderId, Pageable pageable);

	Page<Order> findByCustomerFullname(String fullname, Pageable pageable);

	Page<Order> findByStatus(Integer status, Pageable pageable);

	Page<Order> findByFromDateAndToDate(Timestamp fromDate, Timestamp toDate, Pageable pageable);

	List<Order> findByCustomer(Customer customer);
}
