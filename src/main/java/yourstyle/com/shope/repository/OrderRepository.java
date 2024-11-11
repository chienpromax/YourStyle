package yourstyle.com.shope.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>  {
    
    // TÌM kiếm đơn hàng theo khách hàng
    List<Order> findByCustomer(Customer customer);

    List<Order> findByCustomerAndStatus(Customer customer, int status);

    List<Order> findByCustomer_CustomerId(Integer customerId);

    @Query("SELECT o FROM Order o WHERE o.customer.customerId = :customerId AND o.status = 1")
    Order findOrderByCustomerIdAndStatus(@Param("customerId") Integer customerId);
    
    
}
