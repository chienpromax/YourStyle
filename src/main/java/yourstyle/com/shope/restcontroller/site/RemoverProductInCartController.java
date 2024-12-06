package yourstyle.com.shope.restcontroller.site;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.repository.OrderDetailRepository;
import yourstyle.com.shope.repository.OrderRepository;
import yourstyle.com.shope.repository.ProductVariantRepository;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.ProductService;

@RestController
@RequestMapping("/api/carts")
public class RemoverProductInCartController {

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;


    @PostMapping("/removeProduct")
    public ResponseEntity<?> removeProductFromCart(@RequestParam("orderDetailId") Integer orderDetailId,
                                                   Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Vui lòng đăng nhập"));
        }
    
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer accountId = userDetails.getAccountId();
    
        Customer customer = customerRepository.findByAccount_AccountId(accountId);
    
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Không tìm thấy khách hàng"));
        }
    
        Optional<OrderDetail> orderDetailOptional = orderDetailRepository.findById(orderDetailId);
        if (orderDetailOptional.isPresent()) {
            OrderDetail orderDetail = orderDetailOptional.get();
    
            ProductVariant productVariant = orderDetail.getProductVariant();
            productVariant.setQuantity(productVariant.getQuantity() + orderDetail.getQuantity());
            productVariantRepository.save(productVariant);
    
            orderDetailRepository.delete(orderDetail);
            return ResponseEntity.ok(Map.of("message", "Sản phẩm đã được xóa khỏi giỏ hàng"));
        }
    
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Không tìm thấy sản phẩm trong giỏ hàng"));
    }
    



}

