package yourstyle.com.shope.validation.admin;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoucherDto implements Serializable {
    private String voucherCode;
    private Short type;
    private String formattedDiscount;

    public VoucherDto(String voucherCode, Short type, String formattedDiscount) {
        this.voucherCode = voucherCode;
        this.type = type;
        this.formattedDiscount = formattedDiscount;
    }

}
