package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Size;
import yourstyle.com.shope.repository.OrderDetailRepository;
import yourstyle.com.shope.service.OrderDetailService;
import yourstyle.com.shope.validation.admin.OrderDetailDto;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {
	@Autowired
	OrderDetailRepository orderDetailRepository;
	@Autowired
	Converter<OrderDetail, OrderDetailDto> OrderDetailToOrderDetailDtoConverter;

	public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository) {
		this.orderDetailRepository = orderDetailRepository;
	}

	@Override
	public <S extends OrderDetail> S save(S entity) {
		return orderDetailRepository.save(entity);
	}

	@Override
	public List<OrderDetail> findAll(Sort sort) {
		return orderDetailRepository.findAll(sort);
	}

	@Override
	public Page<OrderDetail> findAll(Pageable pageable) {
		return orderDetailRepository.findAll(pageable);
	}

	@Override
	public List<OrderDetail> findAll() {
		return orderDetailRepository.findAll();
	}

	@Override
	public Optional<OrderDetail> findById(Integer id) {
		return orderDetailRepository.findById(id);
	}

	@Override
	public void deleteById(Integer id) {
		orderDetailRepository.deleteById(id);
	}

	@Override
	public List<OrderDetail> findByOrder(Order order) {
		return orderDetailRepository.findByOrder(order);
	}

	@Override
	public List<OrderDetail> findByOrderAndProductVariantAndSizeAndColor(Order order, ProductVariant productVariant,
			Size size, Color color) {
		return orderDetailRepository.findByOrderAndProductVariantAndSizeAndColor(order, productVariant, size, color);
	}

	@Override
	public Optional<OrderDetail> findOrderDetailByOrderAndProductVariant(Integer orderId, Integer productVariantId,
			Integer colorId, Integer sizeId) {
		return orderDetailRepository.findOrderDetailByOrderAndProductVariant(orderId, productVariantId, colorId,
				sizeId);
	}

}
