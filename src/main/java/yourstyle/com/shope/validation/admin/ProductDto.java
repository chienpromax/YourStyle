package yourstyle.com.shope.validation.admin;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yourstyle.com.shope.model.Category;
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto implements Serializable {

    private Integer productId;

    @NotBlank(message = "[ Tên sản phẩm không được để trống ]")
    @Size(max = 255, message = "[ Tên sản phẩm không được dài hơn 255 ký tự ]")
    private String name;

    private String description;

    @NotNull(message = "[ Trạng thái sản phẩm không được để trống ]")
    private Boolean status;

    @NotNull(message = "[ Giá sản phẩm không được để trống ]")
    @DecimalMin(value = "0.0", inclusive = false, message = "[ Giá sản phẩm phải lớn hơn 0 ]")
    private BigDecimal price;

    private String image;

    private String productDetail;

    @NotNull(message = "[ Phải chọn 1 doanh mục ]")
    private Category category;

    @Transient
    private boolean isEdit = false;

    @Transient
    @NotNull(message = "[ Ảnh sản phẩm không được để trống ]")
    private MultipartFile imageFile;
}
