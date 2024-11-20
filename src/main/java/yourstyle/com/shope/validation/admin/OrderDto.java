package yourstyle.com.shope.validation.admin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto implements Serializable {
    private BigDecimal totalAmount;
    private String transactionType;
    private Timestamp transactionTime;
    private String paymentMethod;
    private String transactionStatus;
    // đơn tại quầy
    private Integer orderId;
    private String fullname;// khách hàng lẻ
    private Timestamp orderDate;
    private String status; // trạng thái đơn
    // chi tiết bán hàng
    private List<OrderDetailDto> orderDetailDtos;
    private VoucherDTO voucherDto;
    // thêm khách hàng cho đơn vận chuyển
    private Integer customerId;

    public OrderDto(BigDecimal totalAmount, String transactionType, Timestamp transactionTime, String paymentMethod,
            String transactionStatus) {
        this.totalAmount = totalAmount;
        this.transactionType = transactionType;
        this.transactionTime = transactionTime;
        this.paymentMethod = paymentMethod;
        this.transactionStatus = transactionStatus;
    }

    public OrderDto(Integer orderId, String fullname, Timestamp orderDate, String status) {
        this.orderId = orderId;
        this.fullname = fullname;
        this.orderDate = orderDate;
        this.status = status;
    }

    public OrderDto(List<OrderDetailDto> orderDetailDtos, VoucherDTO voucherDto) {
        this.orderDetailDtos = orderDetailDtos;
        this.voucherDto = voucherDto;
    }

    // thêm khách hàng cho đơn vận chuyển
    public OrderDto(Integer orderId, Integer customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }

    public OrderDto(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
