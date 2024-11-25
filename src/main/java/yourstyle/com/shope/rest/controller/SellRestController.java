package yourstyle.com.shope.rest.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderChannel;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Voucher;
import yourstyle.com.shope.service.AddressService;
import yourstyle.com.shope.service.ColorService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderDetailService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.ProductVariantService;
import yourstyle.com.shope.service.SizeService;
import yourstyle.com.shope.service.VoucherService;
import yourstyle.com.shope.validation.admin.AddressDto;
import yourstyle.com.shope.validation.admin.OrderDetailDto;
import yourstyle.com.shope.validation.admin.OrderDto;
import yourstyle.com.shope.validation.admin.VoucherDTO;

@CrossOrigin("*")
@RestController
@RequestMapping("api/admin/sell")
public class SellRestController {
        @Autowired
        OrderService orderService;
        @Autowired
        ProductVariantService productVariantService;
        @Autowired
        OrderDetailService orderDetailService;
        @Autowired
        SizeService sizeService;
        @Autowired
        ColorService colorService;
        @Autowired
        CustomerService customerService;
        @Autowired
        VoucherService voucherService;
        @Autowired
        AddressService addressService;

        public Map<Integer, Integer> totalQuantities(List<Order> list) {
                Map<Integer, Integer> totalQuantitiesMap = new HashMap<>();
                for (Order order : list) {
                        List<OrderDetail> orderDetails = order.getOrderDetails();
                        if (orderDetails != null) {
                                // tính tổng số lượng trong đơn hàng chi tiết của mỗi đơn hàng
                                int totalQuantity = orderDetails.stream().mapToInt((OrderDetail::getQuantity)).sum();
                                totalQuantitiesMap.put(order.getOrderId(), totalQuantity);
                        }
                }
                return totalQuantitiesMap;
        }

        public Map<Integer, String> totalAmounts(List<Order> list) {
                Map<Integer, String> totalAmountMap = new HashMap<>();
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
                symbols.setGroupingSeparator('.'); // Dùng dấu '.' cho hàng nghìn
                symbols.setDecimalSeparator(','); // Dùng dấu ',' cho phần thập phân
                DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // định dạng
                for (Order order : list) {
                        String totalAmount = formatter
                                        .format(order.getTotalAmount().setScale(0, RoundingMode.DOWN)) + ".000 VND";
                        totalAmountMap.put(order.getOrderId(), totalAmount);
                }
                return totalAmountMap;
        }

        @GetMapping("saveOrUpdate")
        public ResponseEntity<List<OrderDto>> createOrderSellInStore() {
                // khách hàng lẻ
                Customer customer = customerService.findById(4).get();
                // tạo một đơn hàng
                Order order = new Order();
                order.setTotalAmount(BigDecimal.valueOf(0)); // tổng tiền 0
                order.setStatus(OrderStatus.PLACED);
                order.setNote("");
                order.setTransactionType(null);
                order.setPaymentMethod(null);
                order.setTransactionStatus(null);
                order.setOrderChannel(OrderChannel.IN_STORE); // tại cửa hàng
                order.setTransactionTime(null);
                order.setCustomer(customer); // khách lẻ
                order.setVoucher(null);
                orderService.save(order);
                // tìm kiếm danh sách đơn hàng theo khách hàng lẻ
                List<Order> orders = orderService.findByCustomer(customer);
                List<OrderDto> orderDtos = new ArrayList<>();
                // tổng số lượng
                Map<Integer, Integer> totalQuantities = totalQuantities(orders);
                // tổng số tiền
                Map<Integer, String> totalAmounts = totalAmounts(orders);
                for (Order orderList : orders) {
                        String orderChanel = orderList.getOrderChannel().getValue();
                        if (orderChanel.equalsIgnoreCase("IN_STORE")) {
                                orderChanel = "Tại quầy";
                        }
                        OrderDto orderDtosReponse = new OrderDto(
                                        orderList.getOrderId(),
                                        orderList.getCustomer().getFullname(),
                                        totalQuantities,
                                        totalAmounts,
                                        orderList.getOrderDate(),
                                        orderChanel,
                                        orderList.getStatus().getDescription());
                        orderDtos.add(orderDtosReponse);
                }
                // sắp xếp thời gian giảm dần
                orderDtos.sort((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
                return ResponseEntity.ok(orderDtos);
        }

        public Voucher getBestVoucherForOrder(List<Voucher> voucherList, BigDecimal orderTotal, Integer customerId) {
                // customerId = 4 là khách hàng lẻ
                boolean isGuestCustomer = (customerId == 4);

                return voucherList.stream()
                                .filter(voucher ->
                                // Kiểm tra điều kiện giá trị đơn hàng tối thiểu và tối đa
                                orderTotal.compareTo(voucher.getMinTotalAmount()) >= 0 &&
                                                (voucher.getMaxTotalAmount() == null || orderTotal
                                                                .compareTo(voucher.getMaxTotalAmount()) <= 0)
                                                &&

                                                // Kiểm tra thời gian hiệu lực
                                                // voucher.getStartDate().before(new Date()) &&
                                                // voucher.getEndDate().after(new Date()) &&
                                                voucher.getStartDate().isBefore(LocalDateTime.now()) &&
                                                voucher.getEndDate().isAfter(LocalDateTime.now()) &&

                                                // Kiểm tra số lần sử dụng của voucher maxuse
                                                Math.max(0, voucher.getMaxUses() - orderService
                                                                .countVoucherUsed(voucher.getVoucherId())) > 0
                                                &&
                                                // Nếu là khách lẻ thì bỏ qua kiểm tra maxUsesUser
                                                (isGuestCustomer || orderService.countVoucherUsedByCustomer(
                                                                voucher.getVoucherId(),
                                                                customerId) < voucher.getMaxUsesUser())
                                                &&

                                                // Kiểm tra quyền riêng tư
                                                (voucher.getIsPublic()
                                                                || voucher.getCustomer().getCustomerId() == customerId))
                                // Sắp xếp voucher theo giá trị giảm giá giảm dần
                                .sorted((v1, v2) -> v2.getDiscountAmount().compareTo(v1.getDiscountAmount()))
                                // Lấy voucher đầu tiên trong danh sách hợp lệ
                                .findFirst()
                                .orElse(null); // Nếu không có voucher nào phù hợp
        }

        public OrderDto listOrderDetailDto(Order order) {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
                symbols.setGroupingSeparator('.'); // Dùng dấu '.' cho hàng nghìn
                symbols.setDecimalSeparator(','); // Dùng dấu ',' cho phần thập phân
                DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // định dạng tiền
                List<OrderDetailDto> orderDetailDtos = new ArrayList<>();
                List<OrderDetail> orderDetails = orderDetailService.findByOrder(order);
                BigDecimal total = BigDecimal.ZERO; // biến cộng dồn
                for (OrderDetail orderDetail : orderDetails) {
                        BigDecimal quantity = BigDecimal.valueOf(orderDetail.getQuantity());
                        BigDecimal price = orderDetail.getPrice().setScale(0, RoundingMode.DOWN);
                        String oldPrice = formatter
                                        .format(orderDetail.getProductVariant().getProduct().getPrice()
                                                        .setScale(0, RoundingMode.FLOOR))
                                        + ".000 VND";
                        String newPrice = formatter.format(
                                        orderDetail.getPrice().setScale(0, RoundingMode.FLOOR)) + ".000 VND";
                        String totalSum = formatter.format(quantity.multiply(price)) + ".000 VND";
                        BigDecimal totalAmount = quantity.multiply(price);
                        // Cộng dồn vào tổng
                        total = total.add(totalAmount);
                        String formattedTotalAmount = formatter
                                        .format(total) + ".000 VND";
                        boolean discount = false;
                        if (orderDetail.getProductVariant().getProduct().getDiscount() != null) {
                                discount = true;
                        } else {
                                discount = false;
                        }
                        String discountPercent = formatter
                                        .format(orderDetail.getProductVariant().getProduct().getDiscount()
                                                        .getDiscountPercent().setScale(0, RoundingMode.DOWN))
                                        + " %";
                        OrderDetailDto orderDetailDtoReponse = new OrderDetailDto(
                                        orderDetail.getProductVariant().getProductVariantId(),
                                        orderDetail.getProductVariant().getColor().getColorId(),
                                        orderDetail.getProductVariant().getSize().getSizeId(),
                                        orderDetail.getProductVariant().getProduct().getImage(),
                                        orderDetail.getProductVariant().getProduct().getName(),
                                        orderDetail.getProductVariant().getColor().getColorName(),
                                        orderDetail.getProductVariant().getSize().getSizeName(),
                                        oldPrice,
                                        newPrice,
                                        orderDetail.getQuantity(),
                                        totalSum,
                                        formattedTotalAmount,
                                        discount,
                                        orderDetail.getOrderDetailId(),
                                        discountPercent);

                        orderDetailDtos.add(orderDetailDtoReponse);
                }
                order.setTotalAmount(total);
                orderService.save(order); // lưu tổng tiền

                List<Voucher> vouchers = voucherService.findAll();
                Voucher voucher = getBestVoucherForOrder(vouchers, order.getTotalAmount(), 4);
                order.setVoucher(voucher); // gán voucher cho đơn
                VoucherDTO voucherDto = null;
                if (order.getVoucher() != null) {
                        Byte type = order.getVoucher().getType();
                        BigDecimal discountAmount = order.getVoucher().getDiscountAmount();
                        String formattedDiscount = "";
                        switch (type) {
                                case 1: // giảm giá trực tiếp
                                        order.setTotalAmount(order.getTotalAmount().subtract(discountAmount));
                                        formattedDiscount = formatter.format(discountAmount) + ".000 VND";
                                        orderService.save(order); // Lưu lại sau khi trừ giảm giá
                                        break;
                                case 2: // giảm giá theo phần trăm
                                        BigDecimal percentageDiscount = order.getTotalAmount()
                                                        .multiply(discountAmount.divide(BigDecimal.valueOf(100)));
                                        // Giới hạn mức giảm ở maxTotalAmount nếu vượt quá
                                        if (order.getVoucher().getMaxTotalAmount() != null
                                                        && percentageDiscount.compareTo(
                                                                        order.getVoucher().getMaxTotalAmount()) > 0) {
                                                percentageDiscount = order.getVoucher().getMaxTotalAmount();
                                        }
                                        // Trừ mức giảm từ tổng tiền đơn hàng
                                        order.setTotalAmount(order.getTotalAmount().subtract(percentageDiscount)); // VND
                                        formattedDiscount = formatter.format(discountAmount) + "%";
                                        orderService.save(order);
                                        break;
                                case 3: // giảm giá vận chuyển
                                        order.setTotalAmount(order.getTotalAmount().subtract(discountAmount));
                                        formattedDiscount = formatter.format(discountAmount) + ".000 VND";
                                        orderService.save(order);
                                        break;
                                // case 4: // giảm giá 100%
                                // order.setTotalAmount(BigDecimal.ZERO);
                                // formattedDiscount = "100%";
                                // break;
                                default:
                                        break;
                        }
                        voucherDto = new VoucherDTO(
                                        order.getVoucher().getVoucherCode(),
                                        type,
                                        formattedDiscount);
                } else {
                        // Xử lý khi không có voucher
                        voucherDto = new VoucherDTO(
                                        "",
                                        null,
                                        "0 VND");
                }
                return new OrderDto(orderDetailDtos, voucherDto); // trả về danh sách
        }

        @DeleteMapping("deleteProduct/{orderDetailId}/{orderId}")
        public ResponseEntity<OrderDto> deleteProductVariant(
                        @PathVariable(name = "orderDetailId") Integer orderDetailId,
                        @PathVariable(name = "orderId") Integer orderId) {
                Optional<OrderDetail> orderDetailOptional = orderDetailService.findById(orderDetailId);

                if (orderDetailOptional.isPresent()) {
                        OrderDetail orderDetail = orderDetailOptional.get();

                        ProductVariant productVariant = orderDetail.getProductVariant();
                        productVariant.setQuantity(productVariant.getQuantity() + orderDetail.getQuantity());
                        // cập nhật lại số lượng sau khi xóa khỏi đơn hàng
                        productVariantService.save(productVariant);

                        orderDetailService.deleteById(orderDetail.getOrderDetailId());// xóa

                        OrderDto orderDto = new OrderDto();
                        Optional<Order> orderOptional = orderService.findById(orderId);
                        if (orderOptional.isPresent()) {
                                Order order = orderOptional.get();
                                List<OrderDetail> orderDetails = order.getOrderDetails();
                                BigDecimal total = BigDecimal.ZERO; // khởi tạo 0
                                for (OrderDetail orderDetailUpdate : orderDetails) {
                                        BigDecimal quantiy = BigDecimal.valueOf(orderDetailUpdate.getQuantity());
                                        BigDecimal price = orderDetailUpdate.getPrice().setScale(0, RoundingMode.DOWN);
                                        BigDecimal totalAmount = quantiy.multiply(price);
                                        total = total.add(totalAmount);
                                }
                                order.setTotalAmount(total);
                                orderService.save(order);
                                // hiển thị lại sản phẩm trong đơn
                                orderDto = listOrderDetailDto(order);
                        }
                        return ResponseEntity.ok(orderDto);
                } else {
                        // Trả về 404 nếu không tìm thấy OrderDetail
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
        }

        @RequestMapping(value = "/addProduct", method = RequestMethod.POST)
        public ResponseEntity<OrderDto> insertProductVariant(
                        @RequestBody OrderDetailDto orderDetailDto) {
                Optional<Order> orderOptional = orderService.findById(orderDetailDto.getOrderId());
                Optional<ProductVariant> productVariantOptional = productVariantService
                                .findById(orderDetailDto.getProductVariantId());
                if (orderDetailDto.getOrderId() != null & orderDetailDto.getProductVariantId() != null
                                && orderDetailDto.getColorId() != null && orderDetailDto.getSizeId() != null) {
                        Order order = orderOptional.get();
                        ProductVariant productVariantUpdate = productVariantOptional.get();
                        Optional<OrderDetail> existingOrderDetailOptional = orderDetailService
                                        .findOrderDetailByOrderAndProductVariant(
                                                        orderDetailDto.getOrderId(),
                                                        orderDetailDto.getProductVariantId(),
                                                        orderDetailDto.getColorId(), orderDetailDto.getSizeId());

                        if (existingOrderDetailOptional.isPresent()) {
                                // Nếu OrderDetail đã tồn tại với cùng size và color, tăng số lượng
                                OrderDetail existingOrderDetail = existingOrderDetailOptional.get();
                                existingOrderDetail.setQuantity(
                                                existingOrderDetail.getQuantity() + orderDetailDto.getQuantity());
                                existingOrderDetail.setPrice(orderDetailDto.getDiscountPrice());
                                orderDetailService.save(existingOrderDetail);
                                // cập nhật số lượng
                                productVariantUpdate.setQuantity(
                                                productVariantUpdate.getQuantity() - existingOrderDetail.getQuantity());
                        } else {
                                // không trùng tạo mới orderDetail
                                OrderDetail orderDetail = new OrderDetail();
                                orderDetail.setOrder(order);
                                orderDetail.setProductVariant(productVariantOptional.get());
                                orderDetail.setPrice(orderDetailDto.getDiscountPrice());
                                orderDetail.setQuantity(orderDetailDto.getQuantity());
                                orderDetailService.save(orderDetail);
                                // cập nhật số lượng
                                productVariantUpdate.setQuantity(
                                                productVariantUpdate.getQuantity() - orderDetail.getQuantity());
                        }
                        productVariantService.save(productVariantUpdate); // cập nhật số lượng
                        // xử lý thông tin đơn hàng và format tổng tiền
                        OrderDto orderDto = listOrderDetailDto(order); // danh sách chi tiết và voucher
                        return ResponseEntity.ok(orderDto);
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        @RequestMapping(value = "/decreaseQuantity", method = RequestMethod.POST)
        public ResponseEntity<OrderDto> decreaseQuantity(
                        @RequestBody OrderDetailDto orderDetailDto) {
                Optional<Order> orderOptional = orderService.findById(orderDetailDto.getOrderId());
                Optional<ProductVariant> productVariantOptional = productVariantService
                                .findById(orderDetailDto.getProductVariantId());
                if (orderDetailDto.getOrderId() != null & orderDetailDto.getProductVariantId() != null
                                && orderDetailDto.getColorId() != null && orderDetailDto.getSizeId() != null) {
                        Order order = orderOptional.get();
                        ProductVariant productVariantUpdate = productVariantOptional.get();
                        // tìm đơn chi tiết
                        Optional<OrderDetail> existingOrderDetailOptional = orderDetailService
                                        .findOrderDetailByOrderAndProductVariant(
                                                        orderDetailDto.getOrderId(),
                                                        orderDetailDto.getProductVariantId(),
                                                        orderDetailDto.getColorId(), orderDetailDto.getSizeId());
                        if (existingOrderDetailOptional.isPresent()) {
                                // Nếu OrderDetail đã tồn tại với cùng size và color, tăng số lượng
                                OrderDetail existingOrderDetail = existingOrderDetailOptional.get();
                                if (existingOrderDetail.getQuantity() > 1) {
                                        existingOrderDetail.setQuantity(existingOrderDetail.getQuantity() - 1);
                                        orderDetailService.save(existingOrderDetail);
                                        // cập nhật số lượng
                                        productVariantUpdate.setQuantity(
                                                        productVariantUpdate.getQuantity() + 1);
                                        productVariantService.save(productVariantUpdate); // cập nhật số lượng
                                } else {
                                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                                }
                        }
                        // xử lý thông tin đơn hàng và format tổng tiền
                        OrderDto orderDto = listOrderDetailDto(order);
                        return ResponseEntity.ok(orderDto);
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        @RequestMapping(value = "/increaseQuantity", method = RequestMethod.POST)
        public ResponseEntity<OrderDto> increaseQuantity(
                        @RequestBody OrderDetailDto orderDetailDto) {
                Optional<Order> orderOptional = orderService.findById(orderDetailDto.getOrderId());
                Optional<ProductVariant> productVariantOptional = productVariantService
                                .findById(orderDetailDto.getProductVariantId());
                if (orderDetailDto.getOrderId() != null & orderDetailDto.getProductVariantId() != null
                                && orderDetailDto.getColorId() != null && orderDetailDto.getSizeId() != null) {
                        Order order = orderOptional.get();
                        ProductVariant productVariantUpdate = productVariantOptional.get();
                        // tìm đơn chi tiết
                        Optional<OrderDetail> existingOrderDetailOptional = orderDetailService
                                        .findOrderDetailByOrderAndProductVariant(
                                                        orderDetailDto.getOrderId(),
                                                        orderDetailDto.getProductVariantId(),
                                                        orderDetailDto.getColorId(), orderDetailDto.getSizeId());
                        if (existingOrderDetailOptional.isPresent()) {
                                // Nếu OrderDetail đã tồn tại với cùng size và color, tăng số lượng
                                OrderDetail existingOrderDetail = existingOrderDetailOptional.get();

                                existingOrderDetail.setQuantity(existingOrderDetail.getQuantity() + 1);
                                orderDetailService.save(existingOrderDetail);
                                // cập nhật số lượng
                                productVariantUpdate.setQuantity(
                                                productVariantUpdate.getQuantity() - 1);
                                productVariantService.save(productVariantUpdate); // cập nhật số lượng

                        }
                        // xử lý thông tin đơn hàng và format tổng tiền
                        OrderDto orderDto = listOrderDetailDto(order);
                        return ResponseEntity.ok(orderDto);
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        @RequestMapping(value = "/selectCustomerForOrder", method = RequestMethod.PUT)
        public ResponseEntity<AddressDto> selectCustomerForOrder(@RequestBody OrderDto orderDto) {
                Optional<Order> orderOptional = orderService.findById(orderDto.getOrderId());
                if (orderOptional.isPresent()) {
                        Order order = orderOptional.get();
                        Customer customer = customerService.findById(orderDto.getCustomerId()).get();
                        order.setCustomer(customer); // cập nhật khách hàng
                        orderService.save(order);
                        List<Address> address = addressService
                                        .findByAddressCustomerID(order.getCustomer().getCustomerId());
                        Address addressDefault = new Address();
                        for (Address checkAddress : address) {
                                if (checkAddress.getIsDefault() == true) {
                                        addressDefault = checkAddress;
                                }
                        }
                        AddressDto addressDtoReponse = new AddressDto(
                                        order.getCustomer().getFullname(),
                                        order.getCustomer().getPhoneNumber(),
                                        addressDefault.getStreet(),
                                        addressDefault.getWard(),
                                        addressDefault.getDistrict(),
                                        addressDefault.getCity());
                        return ResponseEntity.ok(addressDtoReponse);
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // hàm xác nhận đặt hàng
        @PutMapping("updateOrder/{orderId}/{status}")
        public ResponseEntity<String> updateOrderInStore(@PathVariable("orderId") Integer orderId,
                        @PathVariable("status") Integer status) {
                System.out.println("mã đơn hàng: " + orderId);
                System.out.println("mã trạng thái: " + status);
                if (orderId != null && status != null) {
                        Order order = orderService.findById(orderId).get();
                        OrderStatus orderStatus = OrderStatus.fromCode(status);
                        order.setStatus(orderStatus);
                        orderService.save(order);
                        return ResponseEntity.ok("Đặt hàng thành công");
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // hàm cập nhật phí ship vào đơn
        @PutMapping("feeShipping/{orderId}/{feeShip}/{isShipping}")
        public ResponseEntity<OrderDto> orderFeeShip(@PathVariable("orderId") Integer orderId,
                        @PathVariable("feeShip") Float feeship, @PathVariable("isShipping") Boolean isShipping) {
                if (orderId != null && feeship != null) {
                        Order order = orderService.findById(orderId).get();
                        BigDecimal feeShipDecimal = BigDecimal.valueOf(feeship);
                        if (isShipping == true) {
                                order.setTotalAmount(order.getTotalAmount().add(feeShipDecimal));
                        } else {
                                order.setTotalAmount(order.getTotalAmount().subtract(feeShipDecimal));
                        }
                        orderService.save(order);
                        OrderDto orderDto = new OrderDto(order.getTotalAmount());
                        return ResponseEntity.ok(orderDto);
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
}
