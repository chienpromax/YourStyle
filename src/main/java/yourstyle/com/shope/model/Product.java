package yourstyle.com.shope.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = true)
    private Boolean status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String productDetail;

    @ManyToOne
    @JoinColumn(name = "categoryId", referencedColumnName = "categoryId", nullable = true)
    private Category category;

    @Transient
    private boolean isEdit = false;

    @Transient // Để không lưu vào database
    private MultipartFile imageFile;

    @Transient
    private Integer totalQuantity; // Số lượng sản phẩm đã bán, chỉ dùng tạm thời

    @JsonIgnore
    @OneToOne(mappedBy = "product")
    @EqualsAndHashCode.Exclude
    private Discount discount;

    @JsonIgnore
    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private List<ProductVariant> productVariants;

    public BigDecimal getDiscountedPrice() {
        if (discount != null
                && discount.getDiscountPercent() != null
                && discount.getStartDate() != null
                && discount.getEndDate() != null) {

            LocalDateTime now = LocalDateTime.now();

            if (now.isAfter(discount.getStartDate()) && now.isBefore(discount.getEndDate())) {
                BigDecimal discountAmount = price.multiply(discount.getDiscountPercent()).divide(new BigDecimal("100"));
                return price.subtract(discountAmount);
            }
        }
        return price;
    }

}
