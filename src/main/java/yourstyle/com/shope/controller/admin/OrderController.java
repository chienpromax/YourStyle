package yourstyle.com.shope.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.*;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.service.OrderDetailService;
import yourstyle.com.shope.service.OrderService;
import java.math.*;
import java.sql.Timestamp;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;

@Controller
@RequestMapping("admin/orders")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderDetailService orderDetailService;

    // xử lý tính tổng số lượng của đơn hàng
    Map<Integer, Integer> totalQuantities = new HashMap<>();
    Map<Integer, BigDecimal> totalAmounts = new HashMap<>();
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
    public String list(Model model, @RequestParam(name = "page", required = false) Optional<Integer> page,
            @RequestParam(name = "size", required = false) Optional<Integer> size,
            @RequestParam(name = "status", required = false) Optional<Integer> status,
            @RequestParam(name = "from_date", required = false) Optional<String> fromDate,
            @RequestParam(name = "to_date", required = false) Optional<String> toDate) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "orderDate"));
        Page<Order> list = null;
        if (status.isPresent()) {
            Integer statusCode = status.get();
            if (statusCode == 7) { // 7 là trạng thái tất cả lọc tất cả đơn hàng
                list = orderService.findAll(pageable);
            } else { // còn lại là trạng thái lọc
                list = orderService.findByStatus(statusCode, pageable);
            }
        } else {
            list = orderService.findAll(pageable);
        }
        if (fromDate.isPresent() && toDate.isPresent()) {
            try {
                // Định dạng DateTimeFormatter cho kiểu yyyy-MM-dd HH:mm:ss
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                // chuyển từ chuổi sang LocalDateTime
                LocalDateTime fromDateTime = LocalDateTime.parse(fromDate.get(), dateTimeFormatter);
                LocalDateTime toDateTime = LocalDateTime.parse(toDate.get(), dateTimeFormatter);
                // chuyển từ LocalDateTime Sang Timestamp
                Timestamp fromDateTimestamp = Timestamp.valueOf(fromDateTime);
                Timestamp toDateTimestamp = Timestamp.valueOf(toDateTime);
                list = orderService.findByFromDateAndToDate(fromDateTimestamp, toDateTimestamp, pageable);
            } catch (DateTimeParseException e) {
                System.err.println("Date format is invalid: " + e.getMessage());
            }
        }
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
        for (Order order : list) {
            // lấy orderId
            Integer orderId = order.getOrderId();
            // Tìm kiếm đơn hàng theo orderID
            Order order2 = orderService.findById(orderId).get();
            if (order2 != null) {
                // lấy đơn hàng vừa tìm được lấy danh sách đơn hàng chi tiết
                List<OrderDetail> orderDetail = order2.getOrderDetails();
                // tính tổng số lượng trong đơn hàng chi tiết của mỗi đơn hàng
                int totalQuantity = orderDetail.stream().mapToInt((OrderDetail::getQuantity)).sum();
                // đưa tổng số lượng vào map
                totalQuantities.put(orderId, totalQuantity);
                // tính tổng tiền
                BigDecimal totalAmount = orderDetail.stream().map(
                        (detail) -> BigDecimal.valueOf(detail.getQuantity()).multiply(detail.getPrice()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add); // cộng dồn tổng tiền
                // đưa tổng tiền vào map
                totalAmounts.put(orderId, totalAmount);
                // cập nhật lại tổng tiền có trong order
                order2.setTotalAmount(totalAmount);
                orderService.save(order2);
            }
        }
        model.addAttribute("totalQuantities", totalQuantities);
        model.addAttribute("totalAmounts", totalAmounts);
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

    @GetMapping("export/excel")
    public ResponseEntity<byte[]> exportOrdersToExcel() throws IOException {
        // Tạo workbook mới
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");
        // tạo header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Mã Hóa Đơn");
        headerRow.createCell(1).setCellValue("Tên khách hàng");
        headerRow.createCell(2).setCellValue("Tổng sản phẩm");
        headerRow.createCell(3).setCellValue("Tổng số tiền");
        headerRow.createCell(4).setCellValue("Ngày tạo");
        headerRow.createCell(5).setCellValue("Trạng thái");
        headerRow.createCell(6).setCellValue("Ghi chú");
        List<Order> orders = orderService.findAll();
        int rowNum = 1;
        for (Order order : orders) {
            Integer orderId = order.getOrderId();
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(order.getOrderId());
            row.createCell(1).setCellValue(order.getCustomer().getFullname());
            row.createCell(2).setCellValue(!totalQuantities.isEmpty() ? totalQuantities.get(orderId) : 0);
            row.createCell(3).setCellValue(order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0);
            row.createCell(4).setCellValue(order.getOrderDate() != null ? order.getOrderDate().toString() : "");
            row.createCell(5).setCellValue(order.getStatusDescription());
            row.createCell(6).setCellValue(order.getNote());
        }
        // xuất ra byte ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        byte[] bytes = outputStream.toByteArray();
        // Đặt tiêu đề
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Oders.xlsx");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}
