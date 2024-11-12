package yourstyle.com.shope.controller.PaymentMethod;

import jakarta.servlet.http.HttpServletRequest;
import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderChannel;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.model.TransactionType;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.VNPayService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.springframework.stereotype.Controller

public class VNPayController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private CustomerService customerService;

    @GetMapping("/vnPayMain")
    public String vnPayMain(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer accountId = userDetails.getAccountId();

        Customer customer = customerService.findByAccountId(accountId);
        if (customer == null) {
            model.addAttribute("errorMessage", "Không tìm thấy khách hàng.");
            return "errorPage";
        }

        List<Order> orders = orderService.findByCustomerAndStatus(customer, 9);
        if (orders.isEmpty()) {
            model.addAttribute("errorMessage", "Không có hóa đơn nào đang xử lý.");
            return "errorPage";
        }

        Order currentOrder = orders.get(0);
        model.addAttribute("orderId", currentOrder.getOrderId());
        model.addAttribute("orderTotal", currentOrder.getTotalAmount());
        model.addAttribute("orderInfo", "Thanh toán đơn hàng #" + currentOrder.getOrderId());
        model.addAttribute("orderDate", currentOrder.getOrderDate());
        model.addAttribute("status", currentOrder.getStatus());

        return "site/VNPays/vnPayMain";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(@RequestParam("orderId") String orderId,
            @RequestParam("amount") double orderTotal,
            @RequestParam("orderInfo") String orderInfo,
            HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl);
        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/vnpay-payment")
    public String getMapping(HttpServletRequest request, Model model) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedPaymentTime = "";
        try {
            Date date = inputFormat.parse(paymentTime);
            formattedPaymentTime = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        double totalPriceVND = Double.parseDouble(totalPrice) / 100;

        // Lấy orderId từ orderInfo
        String orderIdString = orderInfo.replaceAll("[^\\d]", "");
        Integer orderId = null;
        try {
            orderId = Integer.parseInt(orderIdString);
        } catch (NumberFormatException e) {
            e.printStackTrace(); // Ghi log lỗi nếu không lấy được orderId
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer accountId = userDetails.getAccountId();

        Customer customer = customerService.findByAccountId(accountId);
        List<Order> orders = orderService.findByCustomerAndStatus(customer, 9);
        Order order = orders.get(0);
        if (paymentStatus == 1 && orderId != null) {
            // Thanh toán thành công
            order.setStatus(OrderStatus.PLACED);
            order.setTransactionType(TransactionType.ONLINE);
            order.setOrderChannel(OrderChannel.ONLINE);
            order.setTransactionStatus("ĐÃ THANH TOÁN");
            orderService.save(order);
            // Cập nhật các trường khác vào cơ sở dữ liệu
        } else if (orderId != null) {
            // Thanh toán thất bại
            order.setTransactionType(TransactionType.ONLINE);
            order.setOrderChannel(OrderChannel.ONLINE);
            order.setTransactionStatus("THẤT BẠI");
            orderService.save(order);
        }

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", totalPriceVND);
        model.addAttribute("paymentTime", formattedPaymentTime);
        model.addAttribute("transactionId", transactionId);

        return paymentStatus == 1 ? "site/VNPays/ordersuccess" : "site/VNPays/orderfail";
    }

}
