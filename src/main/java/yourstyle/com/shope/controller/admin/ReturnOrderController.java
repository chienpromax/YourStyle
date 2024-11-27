package yourstyle.com.shope.controller.admin;

import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.model.OrderStatusHistory;
import yourstyle.com.shope.service.OrderDetailService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.OrderStatusHistoryService;

@Controller
@RequestMapping("admin/returnOrder")
public class ReturnOrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderDetailService orderDetailService;
    @Autowired
    OrderStatusHistoryService orderStatusHistoryService;

    public void paginationOrders(Page<Order> list, int currentPage, Model model) {
        int totalPages = list.getTotalPages(); // lấy tổng số trang
        if (totalPages > 0) {
            // 1 2 3 4 5
            int start = Math.max(1, currentPage + 1 - 2);
            int end = Math.min(currentPage + 1 + 2, totalPages);
            if (totalPages > 5) {
                if (end == totalPages) {
                    start = end - 5;
                } else if (start == 1) {
                    end = start + 5;
                }
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
    }

    // xử lý tính tổng số lượng của đơn hàng
    Map<Integer, Integer> totalQuantities = new HashMap<>();
    Map<Integer, String> totalAmounts = new HashMap<>();

    public void totalQuantitiesAndTotalAmounts(Page<Order> list, Model model) {
        // xử lý thông tin đơn hàng và format tổng tiền
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.'); // Dùng dấu '.' cho hàng nghìn
        symbols.setDecimalSeparator(','); // Dùng dấu ',' cho phần thập phân
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // định dạng tiền
        for (Order order : list) {
            // lấy orderId
            Integer orderId = order.getOrderId();
            // Tìm kiếm đơn hàng theo orderID
            Order order2 = orderService.findById(orderId).get();
            if (order2 != null) {
                // lấy đơn hàng vừa tìm được lấy danh sách đơn hàng chi tiết
                List<OrderDetail> orderDetails = order2.getOrderDetails();
                // tính tổng số lượng trong đơn hàng chi tiết của mỗi đơn hàng
                int totalQuantity = orderDetails.stream().mapToInt((OrderDetail::getQuantity)).sum();
                // đưa tổng số lượng vào map
                totalQuantities.put(orderId, totalQuantity);
                totalAmounts.put(orderId,
                        formatter.format(order2.getTotalAmount().setScale(0, RoundingMode.FLOOR)) + ".000 VND");
                // chia sẻ tổng số lượng và tổng tiền qua model để hiển thị trong view
                model.addAttribute("totalQuantities", totalQuantities);
                model.addAttribute("totalAmounts", totalAmounts);
            }
        }
    }

    @GetMapping("")
    public String searchReturnOrder(Model model, @RequestParam(name = "page", required = false) Optional<Integer> page,
            @RequestParam(name = "size", required = false) Optional<Integer> size) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(10);
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "orderDate"));
        Page<Order> orders = orderService.findByStatus(8, pageable);
        totalQuantitiesAndTotalAmounts(orders, model);
        paginationOrders(orders, currentPage, model);
        model.addAttribute("orders", orders);
        return "/admin/returnOrder/search";
    }

    @SuppressWarnings("static-access")
    @GetMapping("updateStatus")
    public String updateReturnOrder(Model model, @RequestParam("orderId") Integer orderId,
            @RequestParam(name = "page", required = false) Optional<Integer> page,
            @RequestParam(name = "size", required = false) Optional<Integer> size) {
        Optional<Order> orderOptional = orderService.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getStatus().getCode() == 6) { // 6 là hoàn thành đến bước 6 mới cho phép trả hàng
                OrderStatus orderStatus = OrderStatus.fromCode(8);
                order.setStatus(orderStatus);
                orderService.save(order); // lưu lại đơn hàng
                // cập nhật thời gian trả hàng
                OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
                orderStatusHistory.setOrder(order);
                orderStatusHistory.setStatus(String.valueOf(orderStatus.RETURNED));
                orderStatusHistory.setStatusTime(new Timestamp(System.currentTimeMillis()));
                orderStatusHistoryService.save(orderStatusHistory);
                // hiển thị
                int currentPage = page.orElse(0);
                int pageSize = size.orElse(10);
                Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "orderDate"));
                Page<Order> orders = orderService.findByStatus(8, pageable);
                totalQuantitiesAndTotalAmounts(orders, model);
                paginationOrders(orders, currentPage, model);
                model.addAttribute("orders", orders);
                return "admin/returnOrder/fragments/orderList :: orderRows";
            } else {
                model.addAttribute("error", "Đơn hàng chưa hoàn thành, không thể trả hàng!");
            }

        } else {
            model.addAttribute("error", "Đơn hàng không tồn tại!");
        }
        return "admin/returnOrder/fragments/orderList :: orderRows";
    }

    @GetMapping("listReturnOrder")
    public String listReturnOrder(@RequestParam("orderId") Integer orderId, Model model) {
        Optional<Order> orderOptional = orderService.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            List<OrderDetail> orderDetails = orderDetailService.findByOrder(order);
            List<Address> addresses = order.getCustomer().getAddresses();
            model.addAttribute("orderDetails", orderDetails);
            model.addAttribute("order", order);
            model.addAttribute("addresses", addresses);
        }
        return "/admin/returnOrder/listReturnOrder";
    }

}