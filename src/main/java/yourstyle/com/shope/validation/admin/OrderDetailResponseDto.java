package yourstyle.com.shope.validation.admin;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Discount;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponseDto implements Serializable {
    private OrderDetail orderDetail;
    private Map<String, String> formatedPrices;
    private Product product;
    private Discount discount;
    private Color color;
    private Size size;
}
