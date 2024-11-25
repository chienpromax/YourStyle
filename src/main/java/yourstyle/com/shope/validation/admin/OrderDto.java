package yourstyle.com.shope.validation.admin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

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
    private Map<Integer, Integer> totalQuantities;
    private Map<Integer, String> totalAmounts;
    private String orderChannel;
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

    public OrderDto(Integer orderId, String fullname, Map<Integer, Integer> totalQuantities,
            Map<Integer, String> totalAmounts, Timestamp orderDate, String orderChannel, String status) {
        this.orderId = orderId;
        this.fullname = fullname;
        this.totalQuantities = totalQuantities;
        this.totalAmounts = totalAmounts;
        this.orderDate = orderDate;
        this.orderChannel = orderChannel;
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
