package yourstyle.com.shope.controller.site.carts;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import yourstyle.com.shope.Exception.VoucherNotFoundException;
import yourstyle.com.shope.Exception.VoucherUsageException;
import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.repository.VoucherRepository;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderService;


@RestController
@RequestMapping("/yourstyle/order")
public class OrderSiteController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/apply-voucher")
    public ResponseEntity<Map<String, Object>> applyDiscount(@RequestBody Map<String, String> payload) {
        String vouchercode = payload.get("vouchercode");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer accountId = userDetails.getAccountId();

        Customer customer = customerService.findByAccountId(accountId);

        if (customer == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Customer not found");
            return ResponseEntity.badRequest().body(response);
        }

        Order order = orderService.findByCustomerAndStatus(customer, 1).get(0);

        BigDecimal totalAmount = order.getTotalAmount();

        BigDecimal newTotalAmount;
        try {
            newTotalAmount = orderService.applyVoucher(vouchercode, totalAmount);
        
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("newTotalAmount", newTotalAmount);
            return ResponseEntity.ok(response);
        
        } catch (VoucherNotFoundException | VoucherUsageException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        
    }
}