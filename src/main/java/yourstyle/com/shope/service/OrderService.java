package yourstyle.com.shope.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;

import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderChannel;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.validation.admin.OrderDto;

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

	Page<Order> findByOrderChannel(OrderChannel orderChannel, Pageable pageable);

	Page<Order> findByOrderChannelNotStatusComplete(OrderChannel orderChannel,
			Integer status, Pageable pageable);

	List<Order> findByCustomer(Customer customer);

	Integer countVoucherUsedByCustomer(Integer voucherId, Integer customerId);

	Integer countVoucherUsed(@Param("voucherId") Integer voucherId);

	Page<Order> findByCustomer(Customer customer, Pageable pageable);

	// thêm giỏ hàng
	public List<Order> findByCustomerAndStatus(Customer customer, int status);

	void addProductToCart(Integer customerid, Integer productVariantId, Integer colorId, Integer sizeId,
			Integer quantity);

	BigDecimal applyVoucher(String voucherCode, BigDecimal totalAmount);

	BigDecimal calculateDiscountedTotal(BigDecimal totalAmount, BigDecimal discountAmount);

	List<Order> findByCustomerOrderByOrderDateDesc(Customer customer);

	boolean existsByVoucherId(Integer voucherId);
}
