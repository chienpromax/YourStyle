package yourstyle.com.shope.rest.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.service.OrderService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/orders")
public class OrderRestController {
    @Autowired
    OrderService orderService;

    @PutMapping("update-status")
    public ResponseEntity<String> updateStatusOrder(@RequestBody Map<String, Integer> data, ModelMap model) {
        Integer orderId = data.get("orderId");
        Integer newStatus = data.get("status");
        if (orderId != null && newStatus != null) {
            Optional<Order> orderOptional = orderService.findById(orderId);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                OrderStatus orderStatus = OrderStatus.fromCode(newStatus);
                order.setStatus(orderStatus);
                orderService.save(order);

                return ResponseEntity.ok("Cập nhật trạng thái đơn hàng thành công");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy đơn hàng!");
            }
        }
        return ResponseEntity.badRequest().body("Dữ liệu không hợp lệ");
    }

}
