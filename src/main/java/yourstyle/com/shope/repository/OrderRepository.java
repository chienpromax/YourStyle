package yourstyle.com.shope.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.validation.admin.CustomerSpendingDto;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
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

}
