package yourstyle.com.shope.validation.admin;

import java.io.Serializable;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yourstyle.com.shope.model.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto implements Serializable {
    private Integer accountId;
    @NotBlank(message = "Vui lòng nhập tên tài khoản!")
    private String username;
    // @NotBlank(message = "Vui lòng nhập mật khẩu!")
    // @Size(min = 8, message = "Mật khẩu ít nhất phải có 8 ký tự!")
    // @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$", message = "Mật khẩu phải chứa ít nhất một chữ số, một ký tự thường, một ký tự in hoa và một ký tự đặc biệt")
    private String password;
    @NotBlank(message = "Vui lòng nhập địa chỉ email!")
    @Email(message = "Địa chỉ email không hợp lệ!")
    private String email;

    private Role role;
    private boolean isEdit = false;
}
