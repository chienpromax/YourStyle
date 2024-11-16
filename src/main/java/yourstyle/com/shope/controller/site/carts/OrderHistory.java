package yourstyle.com.shope.controller.site.carts;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderService;

@Controller
@RequestMapping("/yourstyle/order")
public class OrderHistory {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/orderhistory")
    public String showOrderHistory(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer accountId = userDetails.getAccountId();

        Customer customer = customerService.findByAccountId(accountId);
        
        List<Order> orders = orderService.findByCustomer(customer);
        orders.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));

        model.addAttribute("orders", orders);

        return "site/carts/orderhistory";
    }
}
