package yourstyle.com.shope.validation.admin;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String description;

    private Boolean status;

    @NotNull(message = "Giá sản phẩm không được để trống")
    private BigDecimal price;

    private String image;

    private String productDetail;

    private Category category;

    @Transient
    private boolean isEdit = false;

    @Transient // Để không lưu vào database
    private MultipartFile imageFile;
}
