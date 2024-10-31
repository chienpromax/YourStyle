package yourstyle.com.shope.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryId")
    private Integer categoryId;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @Column(name = "status", columnDefinition = "BIT DEFAULT TRUE")
    private Boolean status = true; // Cột trạng thái với giá trị mặc định là TRUE.

    // Cột parent_id liên kết đến category cha, có thể là NULL.
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @Transient // Để không lưu vào database
    private MultipartFile imageFile;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> childCategories;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

}
