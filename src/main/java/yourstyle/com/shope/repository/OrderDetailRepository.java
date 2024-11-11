package yourstyle.com.shope.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    List<OrderDetail> findByOrder_Customer_CustomerId(Integer customerId);

    List<OrderDetail> findByOrder_Customer_CustomerIdAndOrder_Status(Integer customerId, int status);

    // @Query("SELECT od FROM OrderDetail od WHERE od.order.orderId = :orderId AND
    // od.productVariant.productVariantId = :productVariantId")
    // Optional<OrderDetail> findOneByOrderAndProductVariant(@Param("orderId")
    // Integer orderId,
    // @Param("productVariantId") Integer productVariantId);

    @Query("SELECT od FROM OrderDetail od WHERE od.order.orderId = :orderId AND od.productVariant.productVariantId = :productVariantId AND od.productVariant.color.colorId = :colorId AND od.productVariant.size.sizeId = :sizeId")
    Optional<OrderDetail> findOneByOrderAndProductVariant(@Param("orderId") Integer orderId,
            @Param("productVariantId") Integer productVariantId,
            @Param("colorId") Integer colorId,
            @Param("sizeId") Integer sizeId);

    List<OrderDetail> findByOrder(Order order);
}
