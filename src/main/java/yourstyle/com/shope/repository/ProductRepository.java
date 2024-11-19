package yourstyle.com.shope.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Page<Product> findByNameContaining(String name, Pageable pageable);

    // Tìm sản phẩm theo categoryId
    List<Product> findByCategory_CategoryId(Integer categoryId);

    // Phương thức để tìm sản phẩm theo danh mục với phân trang
    Page<Product> findByCategory_CategoryId(Integer categoryId, Pageable pageable);

    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Product p WHERE p.category.categoryId = ?1 AND p.productId != ?2")
    List<Product> findSimilarProducts(Integer categoryId, Integer productId);

    //Lấy Sp bán chạy nhất
    @Query(value = """
                SELECT p.productId, p.name, p.price, p.image, SUM(od.quantity) AS totalQuantity
                FROM orderDetails od
                JOIN productVariants pv ON od.productVariantId = pv.productVariantId
                JOIN products p ON pv.productId = p.productId
                GROUP BY p.productId, p.name, p.price, p.image
                ORDER BY totalQuantity DESC
            """, nativeQuery = true)
            
    List<Object[]> findBestSellingProducts();

    //Lấy sản phâm có mã giảm giá
    @Query("SELECT p FROM Product p WHERE p.discount IS NOT NULL")
    List<Product> findDiscountedProducts();

    List<Product> findByDiscount_discountId(Integer discountId);

    


}
