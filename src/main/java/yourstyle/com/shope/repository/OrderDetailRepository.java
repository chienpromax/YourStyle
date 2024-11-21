package yourstyle.com.shope.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.validation.admin.ProductSalesDto;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
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
                        "od.productVariant.product.name, SUM(od.quantity), SUM(od.quantity * od.price)) "
                        +
                        "FROM OrderDetail od WHERE YEAR(od.order.transactionTime) = YEAR(CURRENT_DATE) AND od.order.transactionStatus LIKE 'Thành công' "
                        +
                        "GROUP BY od.productVariant.product.productId " +
                        "ORDER BY SUM(od.quantity) DESC")
        List<ProductSalesDto> findTopSellingProductsThisYear(Pageable pageable);
}
