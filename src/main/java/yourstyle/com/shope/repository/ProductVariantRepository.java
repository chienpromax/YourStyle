package yourstyle.com.shope.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.ProductVariant;

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
}
