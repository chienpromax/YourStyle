package yourstyle.com.shope.controller.site.carts;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.OrderStatusHistoryService;

@Controller
@RequestMapping("/yourstyle/order")
public class OrderHistoryDetailController {

        @Autowired
        private OrderService orderService;
        @Autowired
        private CustomerRepository customerRepository;
        @Autowired
        private AccountService accountService;
        @Autowired
        OrderStatusHistoryService orderStatusHistoryService;

        @PostMapping("/complete/{id}")
        public String completeOrder(@PathVariable("id") Integer orderId, RedirectAttributes redirectAttributes) {
                Order order = orderService.findById(orderId)
                                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

                order.setStatus(OrderStatus.COMPLETED);
                order.setTransactionStatus("ĐÃ HOÀN THÀNH");
                orderService.save(order);

                redirectAttributes.addFlashAttribute("message", "Đơn hàng đã hoàn thành.");
                return "redirect:/yourstyle/order/orderhistorydetail/" + orderId;
        }

        @PostMapping("/cancel/{id}")
        public String cancelOrder(@PathVariable("id") Integer orderId, RedirectAttributes redirectAttributes) {
                Order order = orderService.findById(orderId)
                                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

                order.setStatus(OrderStatus.CANCELED);
                order.setTransactionStatus("ĐÃ HỦY ĐƠN");
                orderService.save(order);

                redirectAttributes.addFlashAttribute("message", "Đơn hàng đã được hủy thành công.");
                return "redirect:/yourstyle/order/orderhistorydetail/" + orderId;
        }

        @GetMapping("/orderhistorydetail/{id}")
        public String showOrderHistoryDetail(@PathVariable("id") Integer orderId, Model model) {
                // Sử dụng findById và xử lý Optional<Order>
                Order order = orderService.findById(orderId)
                                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

                model.addAttribute("order", order);
                model.addAttribute("orderDetails", order.getOrderDetails());

                CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getPrincipal();
                Integer accountId = customUserDetails.getAccountId();
                Optional<Account> optionalAccount = accountService.findById(accountId);
                Account account = optionalAccount.get();
                Customer customer = customerRepository.findCustomerWithAddresses(account.getCustomer().getCustomerId())
                                .orElse(null);
                List<Address> addresses = customer != null ? customer.getAddresses() : new ArrayList<>();

                Map<String, Timestamp> latestStatusTime = orderStatusHistoryService.getLatestStatusTime(orderId);
                model.addAttribute("latestStatusTime", latestStatusTime);

                model.addAttribute("customer", customer);
                model.addAttribute("addresses", addresses);

                Address defaultAddress = addresses.stream()
                                .filter(Address::getIsDefault)
                                .findFirst()
                                .orElse(null);
                model.addAttribute("defaultAddress", defaultAddress);

                return "site/carts/orderhistorydetail";
        }
}
