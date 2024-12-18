package yourstyle.com.shope.controller.site.carts;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import yourstyle.com.shope.model.Discount;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderChannel;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.model.TransactionType;
import yourstyle.com.shope.model.Voucher;
import yourstyle.com.shope.repository.OrderDetailRepository;
import yourstyle.com.shope.repository.OrderRepository;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.VoucherService;
import yourstyle.com.shope.service.impl.VoucherServiceImpl;

@RestController
@RequestMapping("/yourstyle/order")
public class OrderSiteController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    VoucherService voucherService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @PostMapping("/place-order")
    public ResponseEntity<Map<String, Object>> placeOrder(@RequestBody Map<String, String> payload) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer accountId = userDetails.getAccountId();

        Customer customer = customerService.findByAccountId(accountId);
        if (customer == null || customer.getFullname() == null || customer.getPhoneNumber() == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Vui lòng cập nhật đầy đủ thông tin cá nhân.");
            return ResponseEntity.badRequest().body(response);
        }

        if (customer.getAddresses() == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Vui lòng cập nhập địa chỉ.");
            return ResponseEntity.badRequest().body(response);
        }

        List<Order> orders = orderService.findByCustomerAndStatus(customer, 9);
        if (orders.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không tìm thấy đơn hàng nào đang xử lý.");
            return ResponseEntity.badRequest().body(response);
        }
        Order order = orders.get(0);

        List<OrderDetail> orderDetails = order.getOrderDetails();
        if (orderDetails == null || orderDetails.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Giỏ hàng của bạn không có sản phẩm. Vui lòng thêm sản phẩm.");
            return ResponseEntity.badRequest().body(response);
        }

        String paymentMethod = payload.get("paymentMethod");
        if ("cod".equalsIgnoreCase(paymentMethod)) {
            order.setTransactionType(TransactionType.COD);
            order.setOrderChannel(OrderChannel.DIRECT);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Phương thức thanh toán không hợp lệ.");
            return ResponseEntity.badRequest().body(response);
        }

        order.setTransactionStatus("ĐANG THANH TOÁN");
        order.setStatus(OrderStatus.PLACED);

        orderService.save(order);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đặt hàng thành công.");
        return ResponseEntity.ok(response);
    }

    private BigDecimal calculateTotalAmountWithOrderDiscount(Order order, List<OrderDetail> orderDetails) {
        BigDecimal totalAmount = orderDetails.stream()
                .map(orderDetail -> {
                    BigDecimal price = orderDetail.getProductVariant().getProduct().getPrice();
                    int quantity = orderDetail.getQuantity();
    
                    // Kiểm tra giảm giá
                    Discount discount = orderDetail.getProductVariant().getProduct().getDiscount();
                    if (discount != null && discount.isValid()) {
                        BigDecimal discountPercent = discount.getDiscountPercent().divide(BigDecimal.valueOf(100));
                        price = price.subtract(price.multiply(discountPercent));
                    }
    
                    return price.multiply(BigDecimal.valueOf(quantity)); // Tính tiền cho từng sản phẩm
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Cộng tổng
    
        if (order.getVoucher() != null && order.getVoucher().getDiscountAmount() != null) {
            BigDecimal orderDiscountPercent = order.getVoucher().getDiscountAmount().divide(BigDecimal.valueOf(100));
            totalAmount = totalAmount.subtract(totalAmount.multiply(orderDiscountPercent));
        }
    
        return totalAmount.max(BigDecimal.ZERO);
    }

    @PostMapping("/apply-voucher")
    public ResponseEntity<Map<String, Object>> applyDiscount(@RequestBody Map<String, String> payload) {
        String vouchercode = payload.get("vouchercode").trim().toUpperCase();
    
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer accountId = userDetails.getAccountId();
    
        Customer customer = customerService.findByAccountId(accountId);
        if (customer == null) {
            return createErrorResponse("Customer not found");
        }
    
        Order order = orderService.findByCustomerAndStatus(customer, 9).get(0);
    
        try {
            // Lấy voucher mới
            Optional<Voucher> voucherOpt = voucherService.findByVoucherCode(vouchercode);
            if (voucherOpt.isEmpty()) {
                return createErrorResponse("Voucher không tồn tại");
            }
    
            Voucher newVoucher = voucherOpt.get();
            LocalDateTime now = LocalDateTime.now();
    
            // Kiểm tra ngày hết hạn và bắt đầu
            if (newVoucher.getStartDate() != null && now.isBefore(newVoucher.getStartDate())) {
                return createErrorResponse("Voucher chưa đến thời gian sử dụng");
            }
            if (newVoucher.getEndDate() != null && now.isAfter(newVoucher.getEndDate())) {
                return createErrorResponse("Voucher đã hết hạn");
            }
            // Kiểm tra giá trị đơn hàng với voucher
            BigDecimal orderTotal = order.getTotalAmount(); // Giả định bạn đã có giá trị tổng của đơn hàng
            if (orderTotal.compareTo(newVoucher.getMinTotalAmount()) < 0) {
                return createErrorResponse("Giá trị đơn hàng chưa đủ để áp dụng voucher này");
            }
            if (newVoucher.getMaxTotalAmount() != null && orderTotal.compareTo(newVoucher.getMaxTotalAmount()) > 0) {
                return createErrorResponse("Giá trị đơn hàng vượt quá giá trị áp dụng của voucher này");
            }
            
            // Kiểm tra nếu voucher đã được áp dụng trước đó
            if (order.getVoucher() != null && order.getVoucher().getVoucherCode().equals(vouchercode)) {
                return createErrorResponse("Voucher đã được áp dụng vào đơn hàng này");
            }
    
            // Kiểm tra nếu đã áp dụng voucher khác trước đó
            if (order.getVoucher() != null && !order.getVoucher().getVoucherCode().equals(vouchercode)) {
                // Hủy voucher cũ
                Voucher oldVoucher = order.getVoucher();
                oldVoucher.setMaxUses(oldVoucher.getMaxUses() + 1);
                voucherService.save(oldVoucher);
            }
    
            // Trừ số lượng voucher mới khi áp dụng
            if (newVoucher.getMaxUses() <= 0) {
                return createErrorResponse("Voucher đã hết số lượt sử dụng");
            }
            newVoucher.setMaxUses(newVoucher.getMaxUses() - 1);
            voucherService.save(newVoucher);
    
            // Áp dụng voucher mới vào đơn hàng
            order.setVoucher(newVoucher);
            
            // Tính toán lại tổng tiền đơn hàng với voucher
            BigDecimal newTotalAmount = calculateTotalAmountWithOrderDiscount(order, order.getOrderDetails());
            order.setTotalAmount(newTotalAmount);
            orderRepository.save(order);
    
            // Trả về phản hồi thành công
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("newTotalAmount", newTotalAmount);
            response.put("voucherId", newVoucher.getVoucherId());
            response.put("remainingUses", newVoucher.getMaxUses());
            return ResponseEntity.ok(response);
    
        } catch (Exception e) {
            return createErrorResponse("Có lỗi xảy ra: " + e.getMessage());
        }
    }
    
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }

}