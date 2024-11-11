// package yourstyle.com.shope.controller.site.carts;

// import java.math.BigDecimal;
// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import yourstyle.com.shope.Exception.VoucherNotFoundException;
// import yourstyle.com.shope.Exception.VoucherUsageException;
// import yourstyle.com.shope.model.Order;
// import yourstyle.com.shope.model.Voucher;
// import yourstyle.com.shope.repository.VoucherRepository;
// import yourstyle.com.shope.service.OrderService;


// @RestController
// @RequestMapping("/yourstyle/order")
// public class OrderSiteController {

//     @Autowired
//     private OrderService orderService;

//     @PostMapping("/apply-discount")
//     public ResponseEntity<Map<String, Object>> applyDiscount(@RequestBody Map<String, String> payload) {
//         String discountCode = payload.get("code");

//         // Giả sử bạn lấy đơn hàng của khách hàng
//         Order order = orderService.findByCustomerAndStatus(customer, 1).get(0);  // Lấy đơn hàng đang đặt (status = 1)

//         // Tính tổng tiền của đơn hàng
//         BigDecimal totalAmount = orderService.getTotalAmount(order);

//         BigDecimal newTotalAmount;
//         try {
//             // Áp dụng mã giảm giá
//             newTotalAmount = orderService.applyVoucher(discountCode, totalAmount);

//             // Tạo phản hồi với kết quả
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("newTotalAmount", newTotalAmount);
//             return ResponseEntity.ok(response);
//         } catch (VoucherNotFoundException | VoucherUsageException e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.badRequest().body(response);
//         }
//     }
// }
