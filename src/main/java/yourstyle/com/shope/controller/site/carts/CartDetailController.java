package yourstyle.com.shope.controller.site.carts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.repository.OrderDetailRepository;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.OrderService;

@Controller
@RequestMapping("/yourstyle/carts")
public class CartDetailController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("cartdetail")
    public String add(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Integer accountId = userDetails.getAccountId();

        Customer customer = customerRepository.findByAccount_AccountId(accountId);
        Integer customerId = customer != null ? customer.getCustomerId() : null;

        if (customerId != null) {
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_Customer_CustomerId(customerId);
            model.addAttribute("orderDetails", orderDetails);
        } else {
            model.addAttribute("orderDetails", new ArrayList<>());
        }
        return "site/carts/cartdetail";
    }

    @PostMapping("/removeProduct")
    public String removeProductFromCart(@RequestParam("orderDetailId") Integer orderDetailId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/site/accounts/login";
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer accountId = userDetails.getAccountId();

        // Lấy customerId từ accountId
        Customer customer = customerRepository.findByAccount_AccountId(accountId);
        if (customer == null) {
            return "redirect:/site/accounts/login";
        }

        Optional<OrderDetail> orderDetail = orderDetailRepository.findById(orderDetailId);
        if (orderDetail.isPresent()) {
            orderDetailRepository.delete(orderDetail.get());
        }

        return "redirect:/yourstyle/carts/cartdetail";
    }

}
