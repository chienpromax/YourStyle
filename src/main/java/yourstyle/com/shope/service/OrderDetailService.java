package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import yourstyle.com.shope.model.OrderDetail;

public interface OrderDetailService {

	void deleteById(Integer id);

	Optional<OrderDetail> findById(Integer id);

	List<OrderDetail> findAll();

	Page<OrderDetail> findAll(Pageable pageable);

	List<OrderDetail> findAll(Sort sort);

	<S extends OrderDetail> S save(S entity);

	List<OrderDetail> findByCustomerId(Integer customerId);

}
