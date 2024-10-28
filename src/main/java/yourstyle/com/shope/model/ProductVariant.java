package yourstyle.com.shope.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "productVariants")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productVariantId;

    @ManyToOne
    @JoinColumn(name = "colorId", nullable = false)
    private Color color;

    @ManyToOne
    @JoinColumn(name = "sizeId", nullable = false)
    private Size size;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    private LocalDateTime updateAt;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now(); // Cập nhật updateAt khi tạo mới
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now(); // Chỉ cập nhật updateAt
    }

    @Transient
    private boolean isEdit = false;

}
