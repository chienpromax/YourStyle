package yourstyle.com.shope.controller.site.carts;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderService;

@Controller
@RequestMapping("/yourstyle/order")
public class OrderHistoryController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/orderhistory")
    public String showOrderHistory(Model model, @RequestParam(required = false) Integer selectedStatus,
            HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer accountId = userDetails.getAccountId();

        Customer customer = customerService.findByAccountId(accountId);

        List<Order> orders = orderService.findByCustomerOrderByOrderDateDesc(customer);

        if (selectedStatus != null && selectedStatus != 7) {
            orders = orders.stream()
                    .filter(order -> order.getStatus().getCode() == selectedStatus)
                    .collect(Collectors.toList());
        }
        List<OrderStatus> orderStatuses = Arrays.asList(OrderStatus.values());

        Map<Integer, String> statusDescriptions = Arrays.stream(OrderStatus.values())
                .collect(Collectors.toMap(OrderStatus::getCode, OrderStatus::getDescription));

        model.addAttribute("orders", orders);
        model.addAttribute("orderStatuses", orderStatuses);
        model.addAttribute("statusDescriptions", statusDescriptions);
        model.addAttribute("selectedStatus", selectedStatus);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "site/carts/orderhistory :: #order-content";
        }

        return "site/carts/orderhistory";
    }

}
