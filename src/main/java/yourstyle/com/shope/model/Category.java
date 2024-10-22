package yourstyle.com.shope.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryId")
    private Integer categoryId; // Đây là khóa chính với tự động tăng.

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name; // Cột tên không cho phép NULL và phải là duy nhất.

    @Column(name = "image", columnDefinition = "TEXT")
    private String image; // Cột ảnh có thể null (mặc định).

    @Column(name = "status", columnDefinition = "BIT DEFAULT TRUE")
    private Boolean status = true; // Cột trạng thái với giá trị mặc định là TRUE.

    // Cột parent_id liên kết đến category cha, có thể là NULL.
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parentCategory; // Đây là quan hệ với chính bảng `Category`.

    // Thêm vào để giữ danh sách các category con, hỗ trợ quan hệ đệ quy.
    // @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    // private List<Category> subCategories;

}
