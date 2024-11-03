package yourstyle.com.shope.controller.admin;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import jakarta.validation.Valid;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.*;

import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.service.AddressService;
import yourstyle.com.shope.service.OrderDetailService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.validation.admin.AddressDto;

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
    @Autowired
    AddressService addressService;
    // xử lý tính tổng số lượng của đơn hàng
    Map<Integer, Integer> totalQuantities = new HashMap<>();
    Map<Integer, BigDecimal> totalAmounts = new HashMap<>();
    OrderStatus[] orderStatus = OrderStatus.getALLOrderStatus();

    @GetMapping("detail/{orderId}")
    public String orderDetail(@PathVariable("orderId") Integer orderId, Model model) {
        Optional<Order> orderOption = orderService.findById(orderId);
        if (orderOption.isPresent()) {
            Order order = orderOption.get();
            // thông tin địa chỉ giao hàng của khách hàng
            model.addAttribute("order", order);
            // thêm địa chỉ cho khách hàng bỏ vào object
            model.addAttribute("address", new AddressDto());
            if (order.getCustomer() != null && order.getCustomer().getCustomerId() != null) {
                model.addAttribute("customer", order.getCustomer());
            }
            // danh sách sản phẩm chi tiết của từng đơn hàng
            if (order.getOrderDetails() != null && order.getOrderId() != null) {
                model.addAttribute("orderDetails", order.getOrderDetails());
            }
            // xử lý thông tin đơn hàng và format tổng tiền
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
            symbols.setGroupingSeparator('.'); // Dùng dấu '.' cho hàng nghìn
            symbols.setDecimalSeparator(','); // Dùng dấu ',' cho phần thập phân
            DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // định dạng tiền

            List<Map<String, String>> orderDetailFormattedPrices = new ArrayList<>();
            // biến tổng tiền dùng để cộng dồn
            BigDecimal totalSum = BigDecimal.ZERO;
            // biến tổng tiền sau khi áp dụng voucher giảm giá
            BigDecimal voucherTotalSum = BigDecimal.ZERO;
            // biến định dạng voucher
            String formattedVoucher = "";
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                Map<String, String> priceMap = new HashMap<>();
                // Giá gốc
                BigDecimal oldPrice = orderDetail.getPrice();
                // đưa vào map để hiển thị giá cũ
                priceMap.put("oldPrice",
                        formatter.format(oldPrice.setScale(0, RoundingMode.FLOOR)) + ".000 VND");
                if (orderDetail.getProductVariant().getProduct().getDiscount() != null) {
                    // lấy phần trăm giảm giá
                    BigDecimal discountPercent = orderDetail.getProductVariant().getProduct().getDiscount()
                            .getDiscountPercent();
                    // lấy giá cũ * phần trăm giảm giá -> giá mới
                    BigDecimal discountedPrice = oldPrice
                            .multiply(BigDecimal.ONE.subtract(discountPercent.divide(BigDecimal.valueOf(100))));
                    // tính cột thành tiền (giá mới đã giảm * số lượng)
                    // Làm tròn xuống bỏ phần thập phân
                    BigDecimal discountIntoMoney = discountedPrice.setScale(0, RoundingMode.FLOOR)
                            .multiply(BigDecimal.valueOf(orderDetail.getQuantity()));
                    // đưa giá sau giảm vào map
                    priceMap.put("discountedPrice",
                            formatter.format(discountedPrice.setScale(0, RoundingMode.FLOOR)) + ".000 VND");
                    // đưa vào map để hiển thị thành tiền
                    priceMap.put("discountIntoMoney",
                            formatter.format(discountIntoMoney) + ".000 VND");
                    // cộng dồn thành tiền đã giảm (hiển thị tổng tiền)
                    totalSum = totalSum.add(discountIntoMoney);
                } else {
                    // Tính thành tiền không giảm giá (số lượng * giá cũ)
                    BigDecimal intoMoney = oldPrice.setScale(0, RoundingMode.FLOOR) // Làm tròn xuống bỏ phần thập phân
                            .multiply(BigDecimal.valueOf(orderDetail.getQuantity()));
                    priceMap.put("intoMoney", formatter.format(intoMoney) + ".000 VND");
                    // cộng dồn thành tiền không giảm (hiển thị tổng tiền)
                    totalSum = totalSum.add(intoMoney);
                }
                // đưa map vào list
                orderDetailFormattedPrices.add(priceMap);
            }
            model.addAttribute("orderDetailFormattedPrices", orderDetailFormattedPrices);
            // tổng tiền
            model.addAttribute("totalSum",
                    formatter.format(totalSum.setScale(0, RoundingMode.FLOOR)) + ".000 VND");
            // tính phiếu giảm giá nếu có
            if (order.getVoucher() != null) {
                // định dạng voucher giảm giá 50.000 VND
                BigDecimal discountVoucher = order.getVoucher().getDiscountAmount().setScale(0,
                        RoundingMode.FLOOR);
                // lấy kiểu giảm giá
                int voucherType = order.getVoucher().getType();
                BigDecimal voucherValue = order.getVoucher().getDiscountAmount().setScale(0, RoundingMode.FLOOR);
                switch (voucherType) {
                    case 1: // giảm giá tiền trực tiếp
                        // tính tổng tiền sau khi giảm voucher
                        voucherTotalSum = totalSum
                                .subtract(voucherValue)
                                .add(BigDecimal.valueOf(32));
                        // định dạng voucher giảm giá 50.000 VND
                        formattedVoucher = formatter.format(discountVoucher) + ".000 VND";
                        break;
                    case 2: // giảm giá tiền theo phần trăm
                        voucherTotalSum = totalSum.multiply(BigDecimal.ONE
                                .subtract(voucherValue.divide(BigDecimal.valueOf(100))))
                                .add(BigDecimal.valueOf(32));
                        // định dạng voucher giảm giá %
                        formattedVoucher = formatter.format(discountVoucher) + "%";
                        break;
                    case 3: // Miển phí vận chuyển nếu đủ điều kiện
                        // thành tiền đơn hàng lớn hơn hoặc bằng 300.000đ thì miển phí vận chuyển cho
                        // đơn hàng đó
                        if (totalSum.compareTo(voucherValue) >= 0) { // trả về 1 và 0
                            voucherTotalSum = totalSum.subtract(BigDecimal.valueOf(32));
                            model.addAttribute("shippingFee", formatter.format(0) + " VND");
                        } else { // trả về - 1 nếu thấp hơn tổng tiền tối thiểu
                            voucherTotalSum = totalSum.add(BigDecimal.valueOf(32));
                            model.addAttribute("shippingFee", formatter.format(32) + ".000 VND");
                        }
                        break;
                }

            } else { // ngược lại không có giảm giá
                voucherTotalSum = totalSum;
                formattedVoucher = "0 VND";
            }
            // Tổng tiền sau khi giảm voucher
            model.addAttribute("voucherTotalSum", formatter.format(voucherTotalSum) + ".000 VND");
            // Định dạng tiền voucher
            model.addAttribute("formattedVoucher", formattedVoucher);
            model.addAttribute("shippingFee", formatter.format(32) + ".000 VND");
            return "admin/orders/addOrEdit";
        } else {
            model.addAttribute("messageType", "error");
            model.addAttribute("messageContent", "Không tìm thấy đơn hàng!");
            return "admin/orders/list";
        }
    }

    public void totalQuantitiesAndAmounts(Page<Order> list, Model model) {
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
                // tính tổng tiền của đơn hàng
                BigDecimal totalAmount = BigDecimal.ZERO;
                for (OrderDetail detail : orderDetail) {
                    BigDecimal itemPrice = detail.getPrice();
                    // áp dụng giảm giá sản phẩm nếu có
                    if (detail.getProductVariant().getProduct().getDiscount() != null) {
                        BigDecimal discountPercent = detail.getProductVariant().getProduct().getDiscount()
                                .getDiscountPercent();
                        BigDecimal discountMultiper = BigDecimal.ONE
                                .subtract(discountPercent.divide(BigDecimal.valueOf(100)));
                        itemPrice = itemPrice.multiply(discountMultiper).setScale(0, RoundingMode.FLOOR);
                    }
                    // tổng tiền (giá đã giảm * số lượng)
                    totalAmount = totalAmount.add(itemPrice.multiply(BigDecimal.valueOf(detail.getQuantity())));
                }
                BigDecimal voucherDiscountAmount = BigDecimal.ZERO;
                if (order.getVoucher() != null) {
                    // lấy kiểu giảm giá
                    int voucherType = order.getVoucher().getType();
                    BigDecimal voucherValue = order.getVoucher().getDiscountAmount().setScale(0, RoundingMode.FLOOR);
                    switch (voucherType) {
                        case 1: // giảm giá tiền trực tiếp
                            voucherDiscountAmount = voucherValue;
                            break;
                        case 2: // giảm giá tiền theo phần trăm
                            voucherDiscountAmount = totalAmount.multiply(voucherValue.divide(BigDecimal.valueOf(100)));
                            break;
                        case 3: // Miển phí vận chuyển nếu đủ điều kiện
                            // thành tiền đơn hàng lớn hơn hoặc bằng 300.000đ thì miển phí vận chuyển cho
                            // đơn hàng đó
                            if (totalAmount.compareTo(order.getVoucher().getMinTotalAmount()) >= 0) { // trả về 1 và 0
                                totalAmount = totalAmount.subtract(BigDecimal.valueOf(32));
                            } else { // trả về - 1 nếu thấp hơn tổng tiền tối thiểu
                                totalAmount = totalAmount.add(BigDecimal.valueOf(32));
                            }
                            break;
                    }
                    // tính tổng tiền
                    BigDecimal finalAmount = totalAmount.subtract(voucherDiscountAmount);
                    totalAmounts.put(orderId, finalAmount);
                    // cập nhật lại tổng tiền có trong order
                    order2.setTotalAmount(finalAmount);
                    orderService.save(order2);
                }
                // chia sẻ tổng số lượng và tổng tiền qua model để hiển thị trong view
                model.addAttribute("totalQuantities", totalQuantities);
                model.addAttribute("totalAmounts", totalAmounts);
            }
        }
    }

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

    @GetMapping("")
    public String list(Model model, @RequestParam(name = "page", required = false) Optional<Integer> page,
            @RequestParam(name = "size", required = false) Optional<Integer> size,
            @RequestParam(name = "status", required = false) Optional<Integer> status,
            @RequestParam(name = "from_date", required = false) Optional<String> fromDate,
            @RequestParam(name = "to_date", required = false) Optional<String> toDate) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(10);
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
        for (Order order : list) {
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                if (orderDetail.getProductVariant() == null) {
                    throw new NullPointerException("productVariant is null");
                }
            }
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
        paginationOrders(list, currentPage, model);
        totalQuantitiesAndAmounts(list, model);
        model.addAttribute("orderStatus", orderStatus);
        model.addAttribute("orders", list);
        return "admin/orders/list";
    }

    @GetMapping("search")
    public String searchAccount(Model model, @RequestParam(name = "value", required = false) String value,
            @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(0); // trang hiện tại
        int pageSize = size.orElse(10); // mặc định hiển thị 10 hóa đơn 1 trang
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
        paginationOrders(list, currentPage, model);
        totalQuantitiesAndAmounts(list, model);
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
