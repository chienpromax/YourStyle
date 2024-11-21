package yourstyle.com.shope.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Size;
import yourstyle.com.shope.validation.admin.ProductSalesDto;

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

        // Thống kê số lượng sản phẩm bán được trong ngày hôm nay
        @Query("SELECT SUM(od.quantity) FROM OrderDetail od WHERE DATE(od.order.transactionTime) = CURRENT_DATE AND od.order.transactionStatus LIKE 'Thành công' ")
        Long countSalesByDateToday();

        // Thống kê số lượng sản phẩm bán được trong tháng này
        @Query("SELECT SUM(od.quantity) FROM OrderDetail od WHERE MONTH(od.order.transactionTime) = MONTH(CURRENT_DATE) AND YEAR(od.order.transactionTime) = YEAR(CURRENT_DATE) AND od.order.transactionStatus LIKE 'Thành công' ")
        Long countSalesByDateThisMonth();

        // Thống kê số lượng sản phẩm bán được trong năm nay
        @Query("SELECT SUM(od.quantity) FROM OrderDetail od WHERE YEAR(od.order.transactionTime) = YEAR(CURRENT_DATE) AND od.order.transactionStatus LIKE 'Thành công' ")
        Long countSalesByDateThisYear();

        // Thống kê sản phẩm bán chạy nhất trong ngày
        @Query("SELECT new yourstyle.com.shope.validation.admin.ProductSalesDto(od.productVariant.product.image, " +
                        "od.productVariant.product.productId, " +
                        "od.productVariant.product.name, SUM(od.quantity), SUM(od.quantity * od.price)) " +
                        "FROM OrderDetail od WHERE DATE(od.order.transactionTime) = CURRENT_DATE AND od.order.transactionStatus LIKE 'Thành công' "
                        +
                        "GROUP BY od.productVariant.product.productId " +
                        "ORDER BY SUM(od.quantity) DESC")
        List<ProductSalesDto> findTopSellingProductsToday(Pageable pageable);

        // Thống kê sản phẩm bán chạy nhất trong tháng
        @Query("SELECT new yourstyle.com.shope.validation.admin.ProductSalesDto(od.productVariant.product.image, " +
                        "od.productVariant.product.productId, " +
                        "od.productVariant.product.name, SUM(od.quantity), SUM(od.quantity * od.price)) "
                        +
                        "FROM OrderDetail od " +
                        "WHERE MONTH(od.order.transactionTime) = MONTH(CURRENT_DATE) AND YEAR(od.order.transactionTime) = YEAR(CURRENT_DATE) "
                        +
                        "AND od.order.transactionStatus LIKE 'Thành công'"
                        +
                        "GROUP BY od.productVariant.product.productId " +
                        "ORDER BY SUM(od.quantity) DESC")
        List<ProductSalesDto> findTopSellingProductsThisMonth(Pageable pageable);

        // Thống kê sản phẩm bán chạy nhất trong năm
        @Query("SELECT new yourstyle.com.shope.validation.admin.ProductSalesDto(od.productVariant.product.image, " +
                        "od.productVariant.product.productId, " +
                        "od.productVariant.product.name, SUM(od.quantity), SUM(od.quantity * od.price)) " +
                        "FROM OrderDetail od WHERE YEAR(od.order.transactionTime) = YEAR(CURRENT_DATE) AND od.order.transactionStatus LIKE 'Thành công' " +
                        "GROUP BY od.productVariant.product.productId " +
                        "ORDER BY SUM(od.quantity) DESC")
        List<ProductSalesDto> findTopSellingProductsThisYear(Pageable pageable);
}
