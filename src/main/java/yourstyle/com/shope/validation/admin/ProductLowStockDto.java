package yourstyle.com.shope.validation.admin;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductLowStockDto {
    private Integer productId;
    private String name;
    private String image;
    private BigDecimal price;
    private Long totalQuantity;
}
