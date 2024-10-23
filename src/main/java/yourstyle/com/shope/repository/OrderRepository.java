package yourstyle.com.shope.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yourstyle.com.shope.model.Order;
import java.sql.Timestamp;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o JOIN o.customer c WHERE o.orderId = ?1")
    Page<Order> findByOrderId(Integer orderId, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.customer c WHERE c.fullname LIKE %?1%")
    Page<Order> findByCustomerFullname(String fullname, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = ?1")
    Page<Order> findByStatus(Integer status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN ?1 AND ?2")
    Page<Order> findByFromDateAndToDate(Timestamp fromDate, Timestamp toDate, Pageable pageable);
}
