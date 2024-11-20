package yourstyle.com.shope.validation.admin;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSalesDto {
    private String image;
    private Integer productId;
    private String productName;
    private Long quantitySold;
    private BigDecimal totalSales;

}
