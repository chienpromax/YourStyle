package yourstyle.com.shope.rest.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.service.ColorService;
import yourstyle.com.shope.service.OrderDetailService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.ProductVariantService;
import yourstyle.com.shope.service.SizeService;
import yourstyle.com.shope.validation.admin.OrderDetailDto;
import yourstyle.com.shope.validation.admin.OrderDetailResponseDto;

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

        @DeleteMapping("deleteProduct/{orderDetailId}")
        public ResponseEntity<String> deleteProductVariant(
                        @PathVariable(name = "orderDetailId", required = false) Integer orderDetailId) {
                Optional<OrderDetail> orderDetail = orderDetailService.findById(orderDetailId);
                if (orderDetail.isPresent()) {
                        orderDetailService.deleteById(orderDetailId);
                        return ResponseEntity.ok("Xóa sản phẩm thành công");
                } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy sản phẩm");
                }
        }

        @PostMapping("addProduct")
        public ResponseEntity<String> insertProductVariant(
                        @RequestBody OrderDetailDto orderDetailDto,
                        Model model) {
                Optional<Order> order = orderService.findById(orderDetailDto.getOrderId());
                Optional<ProductVariant> productVariant = productVariantService
                                .findById(orderDetailDto.getProductVariantId());
                if (orderDetailDto.getOrderId() != null & orderDetailDto.getProductVariantId() != null
                                && orderDetailDto.getColorId() != null && orderDetailDto.getSizeId() != null) {
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
                                existingOrderDetail.setPrice(orderDetailDto.getPrice());
                                orderDetailService.save(existingOrderDetail);

                        } else {
                                // không trùng tạo mới orderDetail
                                OrderDetail orderDetail = new OrderDetail();
                                orderDetail.setOrder(order.get());
                                orderDetail.setProductVariant(productVariant.get());
                                orderDetail.setPrice(orderDetailDto.getPrice());
                                orderDetail.setQuantity(orderDetailDto.getQuantity());
                                orderDetailService.save(orderDetail);
                        }

                        // List<OrderDetail> orderDetails = orderDetailService.findByOrder(order.get());
                        // List<OrderDetailResponseDto> orderDetailResponseDtos = new ArrayList<>();
                        // xử lý thông tin đơn hàng và format tổng tiền
                        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
                        symbols.setGroupingSeparator('.'); // Dùng dấu '.' cho hàng nghìn
                        symbols.setDecimalSeparator(','); // Dùng dấu ',' cho phần thập phân
                        DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // định dạng tiền
                        // biến tổng tiền dùng để cộng dồn
                        // BigDecimal totalSum = BigDecimal.ZERO;
                        // biến tổng tiền sau khi áp dụng voucher giảm giá
                        // BigDecimal voucherTotalSum = BigDecimal.ZERO;
                        // biến định dạng voucher
                        // String formattedVoucher = "";
                        // for (OrderDetail orderDetail : orderDetails) {
                        // Map<String, String> priceMap = new HashMap<>();
                        // // Giá gốc
                        // BigDecimal oldPrice = orderDetail.getPrice();
                        // // đưa vào map để hiển thị giá cũ
                        // priceMap.put("oldPrice",
                        // formatter.format(oldPrice.setScale(0, RoundingMode.FLOOR))
                        // + ".000 VND");
                        // if (orderDetail.getProductVariant().getProduct().getDiscount() != null) {
                        // // lấy phần trăm giảm giá
                        // BigDecimal discountPercent = orderDetail.getProductVariant().getProduct()
                        // .getDiscount()
                        // .getDiscountPercent();
                        // // lấy giá cũ * phần trăm giảm giá -> giá mới
                        // BigDecimal discountedPrice = oldPrice
                        // .multiply(BigDecimal.ONE.subtract(discountPercent
                        // .divide(BigDecimal.valueOf(100))));
                        // // tính cột thành tiền (giá mới đã giảm * số lượng)
                        // // Làm tròn xuống bỏ phần thập phân
                        // BigDecimal discountIntoMoney = discountedPrice.setScale(0,
                        // RoundingMode.FLOOR)
                        // .multiply(BigDecimal.valueOf(orderDetail.getQuantity()));
                        // // đưa giá sau giảm vào map
                        // priceMap.put("discountedPrice",
                        // formatter.format(
                        // discountedPrice.setScale(0, RoundingMode.FLOOR))
                        // + ".000 VND");
                        // // đưa vào map để hiển thị thành tiền
                        // priceMap.put("discountIntoMoney",
                        // formatter.format(discountIntoMoney) + ".000 VND");
                        // // cộng dồn thành tiền đã giảm (hiển thị tổng tiền)
                        // totalSum = totalSum.add(discountIntoMoney);
                        // } else {
                        // // Tính thành tiền không giảm giá (số lượng * giá cũ)
                        // BigDecimal intoMoney = oldPrice.setScale(0, RoundingMode.FLOOR) // Làm tròn
                        // // xuống bỏ phần
                        // // thập phân
                        // .multiply(BigDecimal.valueOf(orderDetail.getQuantity()));
                        // priceMap.put("intoMoney", formatter.format(intoMoney) + ".000 VND");
                        // // cộng dồn thành tiền không giảm (hiển thị tổng tiền)
                        // totalSum = totalSum.add(intoMoney);
                        // }
                        // OrderDetailResponseDto dto = new OrderDetailResponseDto();
                        // dto.setOrderDetail(orderDetail);
                        // dto.setFormatedPrices(priceMap);
                        // dto.setProduct(orderDetail.getProductVariant().getProduct());
                        // dto.setDiscount(orderDetail.getProductVariant().getProduct().getDiscount());
                        // dto.setColor(orderDetail.getProductVariant().getColor());
                        // dto.setSize(orderDetail.getProductVariant().getSize());
                        // orderDetailResponseDtos.add(dto);
                        // }

                        return ResponseEntity.ok().body("Thêm sản phẩm thành công");
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi xử lý phía server");
        }
}
