package yourstyle.com.shope.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Size;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
        @Query("SELECT od FROM OrderDetail od JOIN FETCH od.productVariant pv JOIN FETCH pv.product JOIN FETCH pv.size JOIN FETCH pv.color WHERE od.order = :order")
        List<OrderDetail> findByOrder(@Param("order") Order order);

        @Query("SELECT od FROM OrderDetail od " +
                        "JOIN od.productVariant pv " +
                        "JOIN pv.color c " +
                        "JOIN pv.size s " +
                        "JOIN od.order o " +
                        "WHERE o.orderId = :orderId " +
                        "AND pv.productVariantId = :productVariantId " +
                        "AND c.colorId = :colorId " +
                        "AND s.sizeId = :sizeId")
        Optional<OrderDetail> findOrderDetailByOrderAndProductVariant(@Param("orderId") Integer orderId,
                        @Param("productVariantId") Integer productVariantId,
                        @Param("colorId") Integer colorId,
                        @Param("sizeId") Integer sizeId);

        @Query("SELECT od FROM OrderDetail od WHERE od.order = :order AND od.productVariant = :productVariant AND od.productVariant.size = :size AND od.productVariant.color = :color")
        List<OrderDetail> findByOrderAndProductVariantAndSizeAndColor(
                        @Param("order") Order order,
                        @Param("productVariant") ProductVariant productVariant,
                        @Param("size") Size size,
                        @Param("color") Color color);

        @Query("SELECT od FROM OrderDetail od WHERE od.order.orderId = :orderId AND od.productVariant.productVariantId = :productVariantId AND od.productVariant.color.colorId = :colorId AND od.productVariant.size.sizeId = :sizeId")
        Optional<OrderDetail> findOneByOrderAndProductVariant(@Param("orderId") Integer orderId,
                        @Param("productVariantId") Integer productVariantId,
                        @Param("colorId") Integer colorId,
                        @Param("sizeId") Integer sizeId);

        List<OrderDetail> findByOrder_Customer_CustomerId(Integer customerId);

        List<OrderDetail> findByOrder_Customer_CustomerIdAndOrder_Status(Integer customerId, int status);
        // List<OrderDetail> findByOrder(Order order);
}
