package yourstyle.com.shope.controller.admin;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Map;
import java.util.stream.*;

import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.service.AddressService;
import yourstyle.com.shope.service.OrderService;
import java.math.*;
import java.util.Locale;

@Controller
@RequestMapping("admin/orders")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    AddressService addressService;
    OrderStatus[] orderStatus = OrderStatus.getALLOrderStatus();

    @GetMapping("detail/{orderId}")
    public String orderDetail(@PathVariable("orderId") Integer orderId, Model model) {
        Optional<Order> orderOption = orderService.findById(orderId);
        if (orderOption.isPresent()) {
            Order order = orderOption.get();
            // Làm tròn xuống bỏ phần thập phân
            BigDecimal totalAmount = order.getTotalAmount().setScale(0, RoundingMode.FLOOR);
            if (totalAmount != null) {
                // xử lý thông tin đơn hàng và format tổng tiền
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
                symbols.setGroupingSeparator('.'); // Dùng dấu '.' cho hàng nghìn
                symbols.setDecimalSeparator(','); // Dùng dấu ',' cho phần thập phân
                DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // định dạng tiền

                String formattedTotalAmount = formatter.format(totalAmount) + ".000";

                model.addAttribute("formattedTotalAmount", formattedTotalAmount + " VND");
                model.addAttribute("order", order);
                // xử lý thông tin địa chỉ giao hàng của khách hàng
                if (order.getCustomer() != null && order.getCustomer().getCustomerId() != null) {
                    model.addAttribute("customer", order.getCustomer());
                }
                // xử lý danh sách sản phẩm
                if (order.getOrderDetails() != null && order.getOrderId() != null) {
                    model.addAttribute("orderDetails", order.getOrderDetails());
                }
                return "admin/orders/addOrEdit";
            }
        }
        model.addAttribute("messageType", "error");
        model.addAttribute("messageContent", "Không tìm thấy đơn hàng!");
        return "admin/orders/list";
    }

    @GetMapping("")
    public String list(Model model, @RequestParam("page") Optional<Integer> page,
            @RequestParam(name = "size", required = false) Optional<Integer> size) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "orderDate"));
        Page<Order> list = orderService.findAll(pageable);
        int totalPages = list.getTotalPages();
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
        // Order order = new Order();
        // OrderStatus order1 = OrderStatus.fromCode(5);
        // order.setStatus(order1);
        model.addAttribute("orderStatus", orderStatus);
        model.addAttribute("orders", list);
        return "admin/orders/list";
    }

    @GetMapping("search")
    public String searchAccount(Model model, @RequestParam(name = "value", required = false) String value,
            @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(0); // trang hiện tại
        int pageSize = size.orElse(5); // mặc định hiển thị 5 hóa đơn 1 trang
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "orderDate"));
        Page<Order> list = null;
        if (StringUtils.hasText(value)) { // kiểm tra có giá trị hay không
            if (checkNumber(value)) {
                Integer orderId = Integer.valueOf(value);
                list = orderService.findByOrderId(orderId, pageable);
            } else {
                list = orderService.findByCustomerFullname(value, pageable);
            }
        } else {
            list = orderService.findAll(pageable);
        }
        int totalPages = list.getTotalPages(); // lấy tổng số trang
        if (totalPages > 0) {
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
        model.addAttribute("orderStatus", orderStatus);
        model.addAttribute("orders", list);
        return "admin/orders/list";
    }

    @SuppressWarnings("unused")
    private boolean checkNumber(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?"); // kiểm tra chuỗi có phải là số hay không
    }

    @PostMapping("filterStatus")
    public ModelAndView filterStatusOrder(@RequestBody Map<String, Integer> statusData, ModelMap model) {
        System.out.println("dữ liệu " + statusData);
        Integer status = statusData.get("status");
        OrderStatus orderStatus = OrderStatus.fromCode(status);
        List<Order> orders = orderService.findByStatus(orderStatus);
        model.addAttribute("orders", orders);
        return new ModelAndView("admin/orders/list");
    }
}
