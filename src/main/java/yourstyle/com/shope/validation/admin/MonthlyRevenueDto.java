package yourstyle.com.shope.validation.admin;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyRevenueDto {
    private int month;
    private BigDecimal totalRevenue;

}
