package yourstyle.com.shope.validation.admin;

import java.io.Serializable;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yourstyle.com.shope.model.Account;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto implements Serializable {

    private Integer customerId;

    @NotBlank(message = "Vui lòng nhập tên đầy đủ")
    @Size(max = 50, message = "Tên không được dài quá 50 ký tự")
    private String fullname;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    @NotNull(message = "Vui lòng chọn giới tính")
    private Boolean gender;

    private String avatar;

    @NotNull(message = "Vui lòng nhập ngày sinh")
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    private Date birthday;

    @NotBlank(message = "Vui lòng nhập địa chỉ email")
    @Email(message = "Địa chỉ email không hợp lệ")
    private String email;

    private Account account;

    private boolean isEdit = false;

    @Transient // Để không lưu vào database
    private MultipartFile imageFile;
}
