package yourstyle.com.shope.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderChannel;
import yourstyle.com.shope.validation.admin.CustomerSpendingDto;

import java.sql.Timestamp;
import java.util.List;

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

        @Query("SELECT o FROM Order o WHERE o.status = ?1 AND o.orderDate BETWEEN ?2 AND ?3")
        Page<Order> findByFromDateAndToDate(Integer status, Timestamp fromDate, Timestamp toDate, Pageable pageable);

        // lọc kênh mua hàng trong danh sách
        Page<Order> findByOrderChannel(OrderChannel orderChannel, Pageable pageable);

        // TÌM kiếm đơn hàng theo khách hàng
        List<Order> findByCustomer(Customer customer);

        // TÌM kiếm đơn hàng theo khách hàng
        Page<Order> findByCustomer(Customer customer, Pageable pageable);

        // đếm số lượng sử dụng voucher của khách hàng
        @Query("SELECT COUNT(o) FROM Order o WHERE o.voucher.voucherId = :voucherId AND o.customer.customerId = :customerId")
        Integer countVoucherUsedByCustomer(@Param("voucherId") Integer voucherId,
                        @Param("customerId") Integer customerId);

        // đếm số lượng voucher đã được sử dụng trong order
        @Query("SELECT COUNT(o) FROM Order o WHERE o.voucher.voucherId = :voucherId")
        Integer countVoucherUsed(@Param("voucherId") Integer voucherId);

        // danh sách đơn tại quầy
        @Query("SELECT o FROM Order o WHERE o.orderChannel LIKE :orderChannel AND NOT o.status = :status")
        Page<Order> findByOrderChannelNotStatusComplete(@Param("orderChannel") OrderChannel orderChannel,
                        @Param("status") Integer status, Pageable pageable);

        // List<Order> findByCustomer(Customer customer);

        List<Order> findByCustomerAndStatus(Customer customer, int status);

        List<Order> findByCustomer_CustomerId(Integer customerId);

        @Query("SELECT o FROM Order o WHERE o.customer.customerId = :customerId AND o.status = 9")
        Order findOrderByCustomerIdAndStatus(@Param("customerId") Integer customerId);

        List<Order> findByCustomerOrderByOrderDateDesc(Customer customer);

        // ==========HUYYYYYYYY=========
        @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE DATE(o.transactionTime) = CURRENT_DATE AND o.transactionStatus LIKE 'Thành công'")
        Long countRevenueByDateToday();

        @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE MONTH(o.transactionTime) = MONTH(CURRENT_DATE) AND YEAR(o.transactionTime) = YEAR(CURRENT_DATE) AND o.transactionStatus LIKE 'Thành công'")
        Long countRevenueByDateThisMonth();

        @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE YEAR(o.transactionTime) = YEAR(CURRENT_DATE) AND o.transactionStatus LIKE 'Thành công'")
        Long countRevenueByDateThisYear();

        // Doanh thu tuần này
        @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE WEEK(o.transactionTime) = WEEK(CURRENT_DATE) AND YEAR(o.transactionTime) = YEAR(CURRENT_DATE) AND o.transactionStatus LIKE 'Thành công'")
        Long countRevenueByDateThisWeek();

        // Doanh thu quý này
        @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE QUARTER(o.transactionTime) = QUARTER(CURRENT_DATE) AND YEAR(o.transactionTime) = YEAR(CURRENT_DATE) AND o.transactionStatus LIKE 'Thành công'")
        Long countRevenueByDateThisQuarter();

        // Tổng chi tiêu của khách hàng trong ngày hôm nay
        @Query("SELECT new yourstyle.com.shope.validation.admin.CustomerSpendingDto(o.customer.customerId,"
                        + "o.customer.fullname, o.customer.phoneNumber, SUM(o.totalAmount), o.customer.gender) "
                        +
                        "FROM Order o " +
                        "WHERE DATE(o.transactionTime) = CURRENT_DATE AND o.transactionStatus LIKE 'Thành công'" +
                        "GROUP BY o.customer.customerId " +
                        "ORDER BY SUM(o.totalAmount) DESC")
        List<CustomerSpendingDto> findTopCustomersBySpendingToday(Pageable pageable);

        @Query("SELECT new yourstyle.com.shope.validation.admin.CustomerSpendingDto(o.customer.customerId," +
                        "o.customer.fullname, o.customer.phoneNumber, SUM(o.totalAmount), o.customer.gender) " +
                        "FROM Order o " +
                        "WHERE MONTH(o.transactionTime) = MONTH(CURRENT_DATE) AND YEAR(o.transactionTime) = YEAR(CURRENT_DATE) "
                        +
                        "AND o.transactionStatus LIKE 'Thành công'"
                        +
                        "GROUP BY o.customer.customerId " +
                        "ORDER BY SUM(o.totalAmount) DESC")
        List<CustomerSpendingDto> findTopCustomersBySpendingThisMonth(Pageable pageable);

        @Query("SELECT new yourstyle.com.shope.validation.admin.CustomerSpendingDto(o.customer.customerId," +
                        "o.customer.fullname, o.customer.phoneNumber, SUM(o.totalAmount), o.customer.gender) " +
                        "FROM Order o " +
                        "WHERE YEAR(o.transactionTime) = YEAR(CURRENT_DATE) AND o.transactionStatus LIKE 'Thành công'" +
                        "GROUP BY o.customer.customerId " +
                        "ORDER BY SUM(o.totalAmount) DESC")
        List<CustomerSpendingDto> findTopCustomersBySpendingThisYear(Pageable pageable);

        // Doanh thu 12 thang
        @Query("SELECT MONTH(o.transactionTime) AS month, SUM(o.totalAmount) AS totalRevenue " +
                        "FROM Order o " +
                        "WHERE YEAR(o.orderDate) = YEAR(CURRENT_DATE) AND o.transactionStatus LIKE 'Thành công'" +
                        "GROUP BY MONTH(o.transactionTime) " +
                        "ORDER BY MONTH(o.transactionTime)")
        List<Object[]> findMonthlyRevenueForCurrentYear();

        @Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.voucher.voucherId = ?1")
        boolean existsByVoucherId(Integer voucherId);

}
