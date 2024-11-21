package yourstyle.com.shope.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Size;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {

    Page<ProductVariant> findByProductNameContainingIgnoreCase(String productName, Pageable pageable);

    @Query("SELECT pv FROM ProductVariant pv JOIN pv.product p ORDER BY p.price")
    List<ProductVariant> findAllOrderByProductPrice();

    // @Query("SELECT pv FROM ProductVariant pv WHERE " +
    // "(:productName IS NULL OR pv.product.name LIKE %:productName%) AND " +
    // "(:color IS NULL OR pv.color = :color) AND " +
    // "(:size IS NULL OR pv.size = :size)")
    // Page<ProductVariant> findByProductNameAndColorAndSize(@Param("productName")
    // String productName,
    // @Param("color") String color,
    // @Param("size") String size,
    // Pageable pageable);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.productId = ?1")
    List<ProductVariant> findByProductId(Integer productId);

    List<ProductVariant> findByProductVariantId(Integer productVariantId);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.name LIKE %?1%")
    List<ProductVariant> findByProductNameContaining(String name);

    List<ProductVariant> findBySize(Size size);

    List<ProductVariant> findByColor(Color color);

    List<ProductVariant> findByProduct(Product product);

    // Đếm số lượng sản phẩm được tạo hôm nay
    @Query("SELECT COUNT(pv) FROM ProductVariant pv WHERE DATE(pv.createAt) = CURRENT_DATE")
    Long countByCreateDateToday();

    // Đếm số lượng sản phẩm được tạo trong tháng này
    @Query("SELECT COUNT(pv) FROM ProductVariant pv WHERE MONTH(pv.createAt) = MONTH(CURRENT_DATE) AND YEAR(pv.createAt) = YEAR(CURRENT_DATE)")
    Long countByCreateDateThisMonth();

    // Đếm số lượng sản phẩm được tạo trong năm này
    @Query("SELECT COUNT(pv) FROM ProductVariant pv WHERE YEAR(pv.createAt) = YEAR(CURRENT_DATE)")
    Long countByCreateDateThisYear();

}
