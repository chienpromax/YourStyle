package yourstyle.com.shope.validation.admin;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSpendingDto {
    private Integer customerId;
    private String fullname;
    private String phoneNumber;
    private BigDecimal totalSpending;
    private Boolean gender;

}
