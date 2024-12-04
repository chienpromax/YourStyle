package yourstyle.com.shope.validation.admin;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.ProductVariant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {

    private Integer voucherId;

    @NotBlank(message = "Mã giảm giá không được để trống")
    @Size(max = 255, message = "Voucher code không vượt quá 255 ký tự")
    private String voucherCode;

    @NotBlank(message = "Tên giảm giá không được để trống")
    @Size(max = 255, message = "Tên giảm giá không vượt quá 255 ký tự")
    private String voucherName;

    @Size(max = 1000, message = "Mô tả không được dài hơn 1000 ký tự")
    private String description;

    @NotNull(message = "Giảm giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giảm giá phải lớn hơn 0")
    private BigDecimal discountAmount;

    @NotNull(message = "Giá trị thấp nhất không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá trị thấp nhất phải lớn hơn 0")
    private BigDecimal minTotalAmount;

    @DecimalMin(value = "0.0", message = "Giá trị lớn nhất phải lớn hơn hoặc bằng 0")
    private BigDecimal maxTotalAmount;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
    private Integer maxUses;

    @NotNull(message = "Số lần sử dụng không được để trống")
    @Min(value = 1, message = "Số lần sử dụng phải lớn hơn hoặc bằng 1")
    private Integer maxUsesUser;

    @NotNull(message = "Loại giảm giá không được để trống")
    private Byte type;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate;

    private Timestamp createAt = new Timestamp(System.currentTimeMillis());

    private Boolean isPublic;

    private List<Integer> customerIds;

    private Account account;

    private String formattedDiscount;

    private String finalTotalAmount;
    private Integer productVariantId;
    private Integer inventoryQuantity;

    public VoucherDTO(String voucherCode, Byte type, String formattedDiscount, String finalTotalAmount,
            Integer productVariantId, Integer inventoryQuantity) {
        this.voucherCode = voucherCode;
        this.type = type;
        this.formattedDiscount = formattedDiscount;
        this.finalTotalAmount = finalTotalAmount;
        this.productVariantId = productVariantId;
        this.inventoryQuantity = inventoryQuantity;
    }

}