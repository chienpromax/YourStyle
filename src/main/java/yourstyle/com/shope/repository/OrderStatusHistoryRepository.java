package yourstyle.com.shope.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.OrderStatusHistory;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Integer> {
    List<OrderStatusHistory> findByOrderOrderId(Integer orderId);

    Optional<OrderStatusHistory> findTopByOrderOrderIdAndStatusOrderByStatusTimeDesc(Integer orderId, String status);
}
