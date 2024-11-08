package yourstyle.com.shope.validation.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO implements Serializable {

    private Integer addressId;

    @NotBlank(message = "Địa chỉ cụ thể không được để trống.")
    @Size(max = 255, message = "Địa chỉ cụ thể không được quá 255 ký tự.")
    private String street;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống.")
    private String city;

    @NotBlank(message = "Quận/Huyện không được để trống.")
    private String district;

    @NotBlank(message = "Xã/Phường không được để trống.")
    private String ward;

    private Boolean isDefault = false;
}
