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

import jakarta.transaction.Transactional;
import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Voucher;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.repository.OrderDetailRepository;
import yourstyle.com.shope.repository.OrderRepository;
import yourstyle.com.shope.repository.ProductVariantRepository;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.ProductService;
import yourstyle.com.shope.service.VoucherService;

@RestController
@RequestMapping("/api/carts")
public class RemoverProductInCartController {

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/removeProduct")
    @Transactional
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

        // Lấy đơn hàng của khách hàng
        Optional<Order> orderOptional = orderRepository.findByCustomerAndStatus(customer, 9).stream().findFirst();
        if (orderOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Không tìm thấy đơn hàng"));
        }

        Order order = orderOptional.get();

        Optional<OrderDetail> orderDetailOptional = orderDetailRepository.findById(orderDetailId);
        if (orderDetailOptional.isPresent()) {
            OrderDetail orderDetail = orderDetailOptional.get();

            // Trả lại số lượng sản phẩm
            ProductVariant productVariant = orderDetail.getProductVariant();
            productVariant.setQuantity(productVariant.getQuantity() + orderDetail.getQuantity());
            productVariantRepository.save(productVariant);

            // Xóa sản phẩm khỏi giỏ hàng
            orderDetailRepository.delete(orderDetail);

            // Kiểm tra và xóa voucher nếu có
            if (order.getVoucher() != null) {
                Voucher voucher = order.getVoucher();

                // Tăng số lần sử dụng lại của voucher
                voucher.setMaxUses(voucher.getMaxUses() + 1);
                voucherService.save(voucher);

                // Xóa voucher khỏi đơn hàng
                order.setVoucher(null);
                orderRepository.save(order); // Đảm bảo lưu thay đổi
            }

            return ResponseEntity.ok(Map.of("message", "Sản phẩm đã được xóa khỏi giỏ hàng và voucher đã bị xóa"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Không tìm thấy sản phẩm trong giỏ hàng"));
    }

}
