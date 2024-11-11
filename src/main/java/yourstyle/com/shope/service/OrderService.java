package yourstyle.com.shope.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;

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

	List<Order> findByCustomer(Customer customer);

	public List<Order> findByCustomerAndStatus(Customer customer, int status);

	// thêm giỏ hàng
	void addProductToCart(Integer customerid, Integer productVariantId, Integer colorId, Integer sizeId,
			Integer quantity);

	BigDecimal applyVoucher(String voucherCode, BigDecimal totalAmount);

}
