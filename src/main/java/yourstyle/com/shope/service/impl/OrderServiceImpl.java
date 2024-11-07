package yourstyle.com.shope.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.repository.OrderDetailRepository;
import yourstyle.com.shope.repository.OrderRepository;
import yourstyle.com.shope.repository.ProductVariantRepository;
import yourstyle.com.shope.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	OrderDetailRepository orderDetailRepository;
	@Autowired
	ProductVariantRepository productVariantRepository;

	public OrderServiceImpl(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public <S extends Order> S save(S entity) {
		return orderRepository.save(entity);
	}

	@Override
	public <S extends Order> List<S> saveAll(Iterable<S> entities) {
		return orderRepository.saveAll(entities);
	}

	@Override
	public <S extends Order> Optional<S> findOne(Example<S> example) {
		return orderRepository.findOne(example);
	}

	@Override
	public List<Order> findAll(Sort sort) {
		return orderRepository.findAll(sort);
	}

	@Override
	public Page<Order> findAll(Pageable pageable) {
		return orderRepository.findAll(pageable);
	}

	@Override
	public List<Order> findAll() {
		return orderRepository.findAll();
	}

	@Override
	public List<Order> findAllById(Iterable<Integer> ids) {
		return orderRepository.findAllById(ids);
	}

	@Override
	public <S extends Order> Page<S> findAll(Example<S> example, Pageable pageable) {
		return orderRepository.findAll(example, pageable);
	}

	@Override
	public Optional<Order> findById(Integer id) {
		return orderRepository.findById(id);
	}

	@Override
	public boolean existsById(Integer id) {
		return orderRepository.existsById(id);
	}

	@Override
	public Order getOne(Integer id) {
		return orderRepository.getOne(id);
	}

	@Override
	public void deleteById(Integer id) {
		orderRepository.deleteById(id);
	}

	@Override
	public Order getById(Integer id) {
		return orderRepository.getById(id);
	}

	@Override
	public <S extends Order> List<S> findAll(Example<S> example) {
		return orderRepository.findAll(example);
	}

	@Override
	public <S extends Order> List<S> findAll(Example<S> example, Sort sort) {
		return orderRepository.findAll(example, sort);
	}

	@Override
	public List<Order> findByCustomer(Customer customer) {
		return orderRepository.findByCustomer(customer);
	}

	@Override
	public List<Order> findByCustomerAndStatus(Customer customer, int status) {
		return orderRepository.findByCustomerAndStatus(customer, status);
	}

	@Override
	public void addProductToCart(Integer customerId, Integer productVariantId) {
		// Tìm đơn hàng có trạng thái chờ thanh toán của khách hàng
		Order order = orderRepository.findOrderByCustomerIdAndStatus(customerId);

		// Nếu chưa có hóa đơn nào, tạo hóa đơn mới
		if (order == null) {
			order = new Order();
			// Tìm Customer bằng customerId
			Customer customer = customerRepository.findById(customerId)
					.orElseThrow(() -> new RuntimeException("Customer không tồn tại với ID: " + customerId));

			order.setCustomer(customer);
			order.setOrderDate(new Timestamp(System.currentTimeMillis()));
			order.setTotalAmount(BigDecimal.ZERO);
			order.setStatus(OrderStatus.fromCode(1));
			orderRepository.save(order);
		}

		// Kiểm tra sản phẩm đã tồn tại trong đơn hàng chưa
		Optional<OrderDetail> existingOrderDetail = orderDetailRepository
				.findOneByOrderAndProductVariant(order.getOrderId(), productVariantId);

		if (existingOrderDetail.isPresent()) {
			// Nếu sản phẩm đã có trong giỏ hàng, tăng số lượng
			OrderDetail orderDetail = existingOrderDetail.get();
			orderDetail.setQuantity(orderDetail.getQuantity() + 1);
			orderDetailRepository.save(orderDetail);
		} else {
			// Nếu sản phẩm chưa có trong giỏ hàng, thêm sản phẩm mới
			Optional<ProductVariant> productVt = productVariantRepository.findById(productVariantId);
			if (productVt.isPresent()) {
				ProductVariant productVariant = productVt.get();
				OrderDetail newOrderDetail = new OrderDetail();
				newOrderDetail.setOrder(order);
				newOrderDetail.setProductVariant(productVariant);
				newOrderDetail.setQuantity(1);

				Product product = productVariant.getProduct();
				newOrderDetail.setPrice(product.getPrice());
				orderDetailRepository.save(newOrderDetail);
			}
		}
	}
}
