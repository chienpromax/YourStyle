package yourstyle.com.shope.restcontroller.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.repository.OrderDetailRepository;
import yourstyle.com.shope.repository.OrderRepository;
import yourstyle.com.shope.repository.ProductRepository;
import yourstyle.com.shope.repository.ProductVariantRepository;
import yourstyle.com.shope.validation.admin.CustomerSpendingDto;
import yourstyle.com.shope.validation.admin.MonthlyRevenueDto;
import yourstyle.com.shope.validation.admin.ProductDto;
import yourstyle.com.shope.validation.admin.ProductLowStockDto;
import yourstyle.com.shope.validation.admin.ProductSalesDto;
import yourstyle.com.shope.validation.admin.ProductWithoutOrders;

@CrossOrigin("*")
@RestController
public class ReportRestController {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/rest/reports/customers/today")
    public Long getCustomerCountToday() {
        return customerRepository.countByCreateDateToday();
    }

    @GetMapping("/rest/reports/customers/thisMonth")
    public Long getCustomerCountThisMonth() {
        return customerRepository.countByCreateDateThisMonth();
    }

    @GetMapping("/rest/reports/customers/thisYear")
    public Long getCustomerCountThisYear() {
        return customerRepository.countByCreateDateThisYear();
    }

    // API thống kê sản phẩm
    @GetMapping("/rest/reports/productVariants/today")
    public Long getProductCountToday() {
        return productVariantRepository.countByCreateDateToday();
    }

    @GetMapping("/rest/reports/productVariants/thisMonth")
    public Long getProductCountThisMonth() {
        return productVariantRepository.countByCreateDateThisMonth();
    }

    @GetMapping("/rest/reports/productVariants/thisYear")
    public Long getProductCountThisYear() {
        return productVariantRepository.countByCreateDateThisYear();
    }

    // API sales
    @GetMapping("/rest/reports/sales/today")
    public Long getSalesToday() {
        if (orderDetailRepository.countSalesByDateToday() != null) {
            return orderDetailRepository.countSalesByDateToday();
        } else {
            return (long) 0;
        }
    }

    @GetMapping("/rest/reports/sales/thisMonth")
    public Long getSalesThisMonth() {
        if (orderDetailRepository.countSalesByDateThisMonth() != null) {
            return orderDetailRepository.countSalesByDateThisMonth();
        } else {
            return (long) 0;
        }
    }

    @GetMapping("/rest/reports/sales/thisYear")
    public Long getSalesThisYear() {
        if (orderDetailRepository.countSalesByDateThisYear() != null) {
            return orderDetailRepository.countSalesByDateThisYear();
        } else {
            return (long) 0;
        }
    }

    // API Revenue
    @GetMapping("/rest/reports/revenue/today")
    public Long getRevenueToday() {
        if (orderRepository.countRevenueByDateToday() != null) {
            return orderRepository.countRevenueByDateToday();
        } else {
            return (long) 0;
        }
    }

    @GetMapping("/rest/reports/revenue/thisMonth")
    public Long getRevenueThisMonth() {
        if (orderRepository.countRevenueByDateThisMonth() != null) {
            return orderRepository.countRevenueByDateThisMonth();
        } else {
            return (long) 0;
        }
    }

    @GetMapping("/rest/reports/revenue/thisYear")
    public Long getRevenueThisYear() {
        if (orderRepository.countRevenueByDateThisYear() != null) {
            return orderRepository.countRevenueByDateThisYear();
        } else {
            return (long) 0;
        }
    }

    @GetMapping("/rest/reports/revenue/thisWeek")
    public Long getRevenueThisWeek() {
        return orderRepository.countRevenueByDateThisWeek() != null
                ? orderRepository.countRevenueByDateThisWeek()
                : 0L;
    }

    @GetMapping("/rest/reports/revenue/thisQuarter")
    public Long getRevenueThisQuarter() {
        return orderRepository.countRevenueByDateThisQuarter() != null
                ? orderRepository.countRevenueByDateThisQuarter()
                : 0L;
    }

    // Top khach hang
    @GetMapping("/rest/reports/topSpending/today")
    public List<CustomerSpendingDto> getTopCustomersToday(Pageable pageable) {
        return orderRepository.findTopCustomersBySpendingToday(PageRequest.of(0, 10));
    }

    @GetMapping("/rest/reports/topSpending/thisMonth")
    public List<CustomerSpendingDto> getTopCustomersThisMonth(Pageable pageable) {
        return orderRepository.findTopCustomersBySpendingThisMonth(PageRequest.of(0, 10));
    }

    @GetMapping("/rest/reports/topSpending/thisYear")
    public List<CustomerSpendingDto> getTopCustomersThisYear(Pageable pageable) {
        return orderRepository.findTopCustomersBySpendingThisYear(PageRequest.of(0, 10));
    }

    // Top san pham ban chay
    @GetMapping("/rest/reports/topSelling/today")
    public List<ProductSalesDto> getTopSellingProductsToday(Pageable pageable) {
        return orderDetailRepository
                .findTopSellingProductsToday(PageRequest.of(0, 10));
    }

    @GetMapping("/rest/reports/topSelling/thisMonth")
    public List<ProductSalesDto> getTopSellingProductsThisMonth(Pageable pageable) {
        return orderDetailRepository
                .findTopSellingProductsThisMonth(PageRequest.of(0, 10));
    }

    @GetMapping("/rest/reports/topSelling/thisYear")
    public List<ProductSalesDto> getTopSellingProductsThisYear(Pageable pageable) {
        return orderDetailRepository
                .findTopSellingProductsThisYear(PageRequest.of(0, 10));
    }

    // Doanh thu 12 thang
    @GetMapping("/rest/reports/revenue/monthly")
    public List<MonthlyRevenueDto> getMonthlyRevenue() {
        List<Object[]> results = orderRepository.findMonthlyRevenueForCurrentYear();
        List<MonthlyRevenueDto> monthlyRevenue = new ArrayList<>();

        for (Object[] result : results) {
            int month = (int) result[0];
            BigDecimal totalRevenue = (BigDecimal) result[1];
            monthlyRevenue.add(new MonthlyRevenueDto(month, totalRevenue));
        }
        return monthlyRevenue;
    }

    @GetMapping("/rest/reports/product/withoutOrders")
    public Page<ProductWithoutOrders> getProductsWithoutOrders(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findProductsWithoutOrders(pageable);
    }

    @GetMapping("/rest/reports/product/lowStock")
    public Page<ProductLowStockDto> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold, // Ngưỡng hết hàng
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findProductsLowStock(threshold, pageable);
    }

}
