package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;

import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Size;

public interface OrderDetailService {

	void deleteById(Integer id);

	Optional<OrderDetail> findById(Integer id);

	List<OrderDetail> findAll();

	Page<OrderDetail> findAll(Pageable pageable);

	List<OrderDetail> findAll(Sort sort);

	<S extends OrderDetail> S save(S entity);

	List<OrderDetail> findByOrder(Order order);

	List<OrderDetail> findByOrderAndProductVariantAndSizeAndColor(Order order,
			ProductVariant productVariant, Size size, Color color);

	Optional<OrderDetail> findOrderDetailByOrderAndProductVariant(
			Integer orderId,
			Integer productVariantId,
			Integer colorId,
			Integer sizeId);
}
