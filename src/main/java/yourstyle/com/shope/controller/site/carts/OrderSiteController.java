package yourstyle.com.shope.controller.site.carts;

import java.math.BigDecimal;
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
            response.put("message", "Vui lòng cập nhật địa chỉ.");
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
        // Tính tổng tiền ban đầu của đơn hàng
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

        // Áp dụng giảm giá mã voucher
        if (order.getVoucher() != null) {
            BigDecimal orderDiscountPercent = order.getVoucher().getDiscountAmount().divide(BigDecimal.valueOf(100));
            totalAmount = totalAmount.subtract(totalAmount.multiply(orderDiscountPercent));
        }

        return totalAmount;
    }

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

        Order order = orderService.findByCustomerAndStatus(customer, 9).get(0);
        List<OrderDetail> orderDetails = orderDetailRepository
                .findByOrder_Customer_CustomerIdAndOrder_Status(customer.getCustomerId(), 9);

        try {
            // Kiểm tra nếu voucher đã được áp dụng
            if (order.getVoucher() != null && order.getVoucher().getVoucherCode().equals(vouchercode)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Voucher đã được áp dụng trước đó");
                return ResponseEntity.badRequest().body(response);
            }

            Optional<Voucher> voucherOpt = voucherService.findByVoucherCode(vouchercode);
            if (voucherOpt.isEmpty()) {
                throw new VoucherNotFoundException("Voucher không tồn tại");
            }

            Voucher voucher = voucherOpt.get();
            order.setVoucher(voucher);

            // Tính lại tổng số tiền dựa trên orderDetails và áp dụng voucher
            BigDecimal newTotalAmount = calculateTotalAmountWithOrderDiscount(order, orderDetails);

            // Cập nhật thông tin đơn hàng với tổng số tiền đã giảm
            order.setTotalAmount(newTotalAmount);
            orderRepository.save(order);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("newTotalAmount", newTotalAmount);
            response.put("voucherId", voucher.getVoucherId());
            return ResponseEntity.ok(response);

        } catch (VoucherNotFoundException | VoucherUsageException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}