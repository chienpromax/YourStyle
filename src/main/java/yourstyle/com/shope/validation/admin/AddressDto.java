package yourstyle.com.shope.validation.admin;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yourstyle.com.shope.model.Customer;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto implements Serializable {
    private Integer addressId;
    @NotBlank(message = "Vui lòng nhập địa chỉ cụ thể!")
    private String street;
    private String ward;
    private String district;
    private String city;
    private boolean isDefault = false;
    private Integer customerId;
    private Customer customer;
}
