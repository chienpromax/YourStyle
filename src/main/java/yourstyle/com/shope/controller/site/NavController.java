package yourstyle.com.shope.controller.site;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Discount;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.repository.OrderDetailRepository;
import yourstyle.com.shope.service.CategoryService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderService;

@ControllerAdvice
public class NavController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderService orderService;
    @Autowired
    CustomerService customerService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @ModelAttribute("parentCategories")
    public List<Category> getParentCategories() {
        return categoryService.findParentCategories();
    }

    @ModelAttribute("orderDetails")
    public List<OrderDetail> populateCartDetails(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Integer accountId = userDetails.getAccountId();
    
            Customer customer = customerRepository.findByAccount_AccountId(accountId);

            Integer customerId = customer != null ? customer.getCustomerId() : null;
    
            if (customerId != null) {
                List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_Customer_CustomerIdAndOrder_Status(customerId, 9);
    
                List<Order> orders = orderService.findByCustomerAndStatus(customer, 9);
                if (!orders.isEmpty()) {
                    Order currentOrder = orders.get(0);
                    BigDecimal totalAmount = calculateTotalAmountWithOrderDiscount(currentOrder, orderDetails);
                    
                    model.addAttribute("totalAmount", totalAmount);
    
                    long uniqueProductVariantCount = orderDetails.stream()
                            .map(OrderDetail::getProductVariant)
                            .distinct()
                            .count();
                    model.addAttribute("cartItemCount", uniqueProductVariantCount);
    
                    return orderDetails;
                }
            }
        }
    
        model.addAttribute("cartItemCount", 0);
        model.addAttribute("totalAmount", BigDecimal.ZERO);
        return new ArrayList<>();
    }
    
    private BigDecimal calculateTotalAmountWithOrderDiscount(Order order, List<OrderDetail> orderDetails) {
        BigDecimal totalAmount = orderDetails.stream()
                .map(orderDetail -> {
                    BigDecimal price = orderDetail.getProductVariant().getProduct().getPrice();
                    int quantity = orderDetail.getQuantity();
    
                    Discount discount = orderDetail.getProductVariant().getProduct().getDiscount();
                    if (discount != null) {
                        BigDecimal discountPercent = discount.getDiscountPercent().divide(BigDecimal.valueOf(100));
                        price = price.subtract(price.multiply(discountPercent));
                    }
    
                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    
        if (order.getVoucher() != null) {
            BigDecimal orderDiscountPercent = order.getVoucher().getDiscountAmount().divide(BigDecimal.valueOf(100));
            totalAmount = totalAmount.subtract(totalAmount.multiply(orderDiscountPercent));
        }
    
        return totalAmount;
    }
    
    @ModelAttribute("currentUser")
    public Customer getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        if (authentication != null && authentication.isAuthenticated() 
                && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Integer accountId = userDetails.getAccountId();
            Customer customer = customerRepository.findByAccount_AccountId(accountId);
    
            if (customer != null) {
                return customer;
            }
        }
    
        return null;
    }
    
}
