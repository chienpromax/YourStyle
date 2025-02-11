package yourstyle.com.shope.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.Exception.VoucherNotFoundException;
import yourstyle.com.shope.Exception.VoucherUsageException;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderChannel;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Voucher;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.repository.OrderDetailRepository;
import yourstyle.com.shope.repository.OrderRepository;
import yourstyle.com.shope.repository.ProductVariantRepository;
import yourstyle.com.shope.repository.VoucherRepository;
import yourstyle.com.shope.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	ProductVariantRepository productVariantRepository;
	@Autowired
	OrderDetailRepository orderDetailRepository;
	@Autowired
	VoucherRepository voucherRepository;

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
	public Page<Order> findByOrderId(Integer orderId, List<OrderChannel> orderChannels, Pageable pageable) {
		return orderRepository.findByOrderId(orderId, orderChannels, pageable);
	}

	@Override
	public Page<Order> findByCustomerFullname(String fullname, Pageable pageable) {
		return orderRepository.findByCustomerFullname(fullname, pageable);
	}

	@Override
	public Page<Order> findByStatus(Integer status, Pageable pageable) {
		return orderRepository.findByStatus(status, pageable);
	}

	@Override
	public Page<Order> findByFromDateAndToDate(Timestamp fromDate, Timestamp toDate, Pageable pageable) {
		return orderRepository.findByFromDateAndToDate(fromDate, toDate, pageable);
	}

	@Override
	public List<Order> findByCustomer(Customer customer) {
		return orderRepository.findByCustomer(customer);
	}

	@Override
	public Integer countVoucherUsedByCustomer(Integer voucherId, Integer customerId) {
		return orderRepository.countVoucherUsedByCustomer(voucherId, customerId);
	}

	@Override
	public Integer countVoucherUsed(Integer voucherId) {
		return orderRepository.countVoucherUsed(voucherId);
	}

	@Override
	public Page<Order> findByOrderChannel(OrderChannel orderChannel, Pageable pageable) {
		return orderRepository.findByOrderChannel(orderChannel, pageable);
	}

	@Override
	public Page<Order> findByCustomer(Customer customer, Pageable pageable) {
		return orderRepository.findByCustomer(customer, pageable);
	}

	@Override
	public Page<Order> findByOrderChannelNotStatusComplete(OrderChannel orderChannel, List<Integer> statuses,
			Pageable pageable) {
		return orderRepository.findByOrderChannelNotStatusComplete(orderChannel, statuses, pageable);
	}

	@Override
	public List<Order> findByCustomerAndStatus(Customer customer, int status) {
		return orderRepository.findByCustomerAndStatus(customer, status);
	}

	@Override
	public void addProductToCart(Integer customerId, Integer productVariantId, Integer colorId, Integer sizeId,
			Integer quantity) {
		Order order = orderRepository.findOrderByCustomerIdAndStatus(customerId);

		if (order == null) {
			order = new Order();
			Customer customer = customerRepository.findById(customerId)
					.orElseThrow(() -> new RuntimeException("Customer không tồn tại với ID: " + customerId));

			order.setCustomer(customer);
			order.setOrderDate(new Timestamp(System.currentTimeMillis()));
			order.setTotalAmount(BigDecimal.ZERO);
			order.setStatus(OrderStatus.fromCode(9));
			orderRepository.save(order);
		}

		Optional<ProductVariant> productVt = productVariantRepository.findById(productVariantId);
		if (productVt.isPresent()) {
			ProductVariant productVariant = productVt.get();

			if (productVariant.getQuantity() >= quantity) {
				Optional<OrderDetail> existingOrderDetail = orderDetailRepository
						.findOneByOrderAndProductVariant(order.getOrderId(), productVariantId, colorId, sizeId);

				BigDecimal priceToUse = productVariant.getProduct().getDiscountedPrice();

				if (existingOrderDetail.isPresent()) {
					OrderDetail orderDetail = existingOrderDetail.get();
					orderDetail.setQuantity(orderDetail.getQuantity() + quantity);
					orderDetail.setPrice(priceToUse);
					orderDetailRepository.save(orderDetail);
				} else {
					OrderDetail newOrderDetail = new OrderDetail();
					newOrderDetail.setOrder(order);
					newOrderDetail.setProductVariant(productVariant);
					newOrderDetail.setQuantity(quantity);
					newOrderDetail.setPrice(priceToUse);
					orderDetailRepository.save(newOrderDetail);
				}

				productVariant.setQuantity(productVariant.getQuantity() - quantity);
				productVariantRepository.save(productVariant);

			} else {
				throw new RuntimeException("Không đủ số lượng sản phẩm trong kho.");
			}
		} else {
			throw new RuntimeException("Product Variant không tồn tại với ID: " + productVariantId);
		}
	}

	@Override
	public BigDecimal applyVoucher(String voucherCode, BigDecimal totalAmount)
			throws VoucherNotFoundException, VoucherUsageException {
		Voucher voucher = voucherRepository.findByVoucherCodeOrder(voucherCode)
				.orElseThrow(() -> new VoucherNotFoundException("Voucher không tìm thấy"));

		// Kiểm tra thời gian hiệu lực
		LocalDateTime now = LocalDateTime.now();
		if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate())) {
			throw new VoucherNotFoundException("Voucher đã hết hạn");
		}

		// Kiểm tra số lần sử dụng
		if (voucher.getMaxUses() <= 0) {
			throw new VoucherUsageException("Voucher đã hết lượt sử dụng");
		}

		// Kiểm tra số tiền tối thiểu và tối đa
		if (totalAmount.compareTo(voucher.getMinTotalAmount()) < 0
				|| (voucher.getMaxTotalAmount() != null && totalAmount.compareTo(voucher.getMaxTotalAmount()) > 0)) {
			throw new VoucherUsageException("Voucher không áp dụng cho hóa đơn này");
		}

		// Áp dụng giảm giá
		BigDecimal discountAmount = voucher.getDiscountAmount();
		if (voucher.getType() == 1) { // Giảm giá phần trăm
			discountAmount = totalAmount.multiply(discountAmount.divide(BigDecimal.valueOf(100)));
			voucher.setMaxUses(voucher.getMaxUses() - 1);
			voucherRepository.save(voucher);

		}

		return totalAmount.subtract(discountAmount);
	}

	@Override
	public BigDecimal calculateDiscountedTotal(BigDecimal totalAmount, BigDecimal discountAmount) {
		return totalAmount.subtract(discountAmount).max(BigDecimal.ZERO);
	}

	@Override
	public List<Order> findByCustomerOrderByOrderDateDesc(Customer customer) {
		return orderRepository.findByCustomerOrderByOrderDateDesc(customer);
	}

	@Override
	public boolean existsByVoucherId(Integer voucherId) {
		return orderRepository.existsByVoucherId(voucherId);
	}

	@Override
	public void removeVoucherFromOrder(Integer customerId) {
		Order currentOrder = orderRepository.findCurrentOrderByCustomerId(customerId);
		if (currentOrder != null && currentOrder.getVoucher() != null) {
			currentOrder.setVoucher(null);
			orderRepository.save(currentOrder);
		}
	}

	@Override
	public Page<Order> findByOrderId(Integer orderId, Pageable pageable) {
		return orderRepository.findByOrderId(orderId, pageable);
	}
}
