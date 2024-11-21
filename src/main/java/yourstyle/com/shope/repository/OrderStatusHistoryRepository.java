package yourstyle.com.shope.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.OrderStatusHistory;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Integer> {
    @Query("SELECT o FROM OrderStatusHistory o WHERE o.order.orderId = :orderId ORDER BY o.statusTime DESC")
    List<OrderStatusHistory> findByOrderOrderId(Integer orderId);

    Optional<OrderStatusHistory> findTopByOrderOrderIdAndStatusOrderByStatusTimeDesc(Integer orderId, String status);

    @Query("SELECT o FROM OrderStatusHistory o WHERE o.order.orderId = :orderId ORDER BY o.statusTime DESC")
    List<OrderStatusHistory> findAllByOrderIdOrderByStatusTimeDesc(@Param("orderId") Integer orderId);
}
