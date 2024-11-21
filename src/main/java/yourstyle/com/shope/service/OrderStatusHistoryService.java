package yourstyle.com.shope.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import yourstyle.com.shope.model.OrderStatusHistory;

public interface OrderStatusHistoryService {

	void deleteById(Integer id);

	Optional<OrderStatusHistory> findById(Integer id);

	List<OrderStatusHistory> findAll();

	<S extends OrderStatusHistory> S save(S entity);

	List<OrderStatusHistory> findByOrderOrderId(Integer orderId);

	Optional<OrderStatusHistory> findByLatestStatus(Integer orderId, String status);

	// lấy trạng thái thời gian mới nhất
	Map<String, Timestamp> getLatestStatusTime(Integer orderId);
}
