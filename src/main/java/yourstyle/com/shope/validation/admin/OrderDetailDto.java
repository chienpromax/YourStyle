package yourstyle.com.shope.validation.admin;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto implements Serializable {
    private Integer orderDetailId;
    private BigDecimal price;
    private Integer quantity;
    private Integer orderId;
    private Integer productVariantId;
    private Integer sizeId;
    private Integer colorId;

    public OrderDetailDto(Integer orderDetailId, Integer productVariantId, Integer orderId, BigDecimal price,
            Integer quantity, Integer sizeId, Integer colorId) {
        this.orderDetailId = orderDetailId;
        this.productVariantId = productVariantId;
        this.orderId = orderId;
        this.price = price;
        this.quantity = quantity;
        this.sizeId = sizeId;
        this.colorId = colorId;
    }

}
