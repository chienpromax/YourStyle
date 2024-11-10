package yourstyle.com.shope.validation.admin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yourstyle.com.shope.model.Order;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto implements Serializable {

    private Integer orderId;
    private BigDecimal totalAmount;
    private String transactionType;
    private Timestamp transactionTime;
    private String paymentMethod;
    private String transactionStatus;

    public OrderDto(BigDecimal totalAmount, String transactionType, Timestamp transactionTime, String paymentMethod,
            String transactionStatus) {
        this.totalAmount = totalAmount;
        this.transactionType = transactionType;
        this.transactionTime = transactionTime;
        this.paymentMethod = paymentMethod;
        this.transactionStatus = transactionStatus;
    }
}
