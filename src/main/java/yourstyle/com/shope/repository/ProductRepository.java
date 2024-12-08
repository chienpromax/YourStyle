package yourstyle.com.shope.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.validation.admin.ProductLowStockDto;
import yourstyle.com.shope.validation.admin.ProductWithoutOrders;
import java.math.BigDecimal;

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

        // Lấy Sp bán chạy nhất
        // @Query(value = """
        // SELECT p.productId, p.name, p.price, p.image, SUM(od.quantity) AS
        // totalQuantity
        // FROM orderDetails od
        // JOIN productVariants pv ON od.productVariantId = pv.productVariantId
        // JOIN products p ON pv.productId = p.productId
        // GROUP BY p.productId, p.name, p.price, p.image
        // ORDER BY totalQuantity DESC
        // """, nativeQuery = true)

        // List<Object[]> findBestSellingProducts();
        @Query(value = """
                            SELECT p.productId, p.name, p.price, p.image, SUM(od.quantity) AS totalQuantity
                            FROM orderDetails od
                            JOIN productVariants pv ON od.productVariantId = pv.productVariantId
                            JOIN products p ON pv.productId = p.productId
                            WHERE p.status = true  -- Chỉ lấy sản phẩm có status = true
                            GROUP BY p.productId, p.name, p.price, p.image
                            ORDER BY totalQuantity DESC
                        """, nativeQuery = true)
        List<Object[]> findBestSellingProducts();

        // Lấy sản phẩm có mã giảm giá và status = true
        @Query("SELECT p FROM Product p WHERE p.discount IS NOT NULL AND p.status = true")
        List<Product> findDiscountedProducts();

        List<Product> findByDiscount_discountId(Integer discountId);

        List<Product> findByCategory(Category category);

        @Query("SELECT new yourstyle.com.shope.validation.admin.ProductWithoutOrders(p.productId, p.name, p.image, p.price, p.status)"
                        + "FROM Product p  WHERE p.productId NOT IN (SELECT od.productVariant.product.productId FROM OrderDetail od)")
        Page<ProductWithoutOrders> findProductsWithoutOrders(Pageable pageable);

        @Query("SELECT new yourstyle.com.shope.validation.admin.ProductLowStockDto(p.productId, p.name, p.image, p.price, SUM(pv.quantity)) "
                        + "FROM Product p " + "LEFT JOIN ProductVariant pv ON p.productId = pv.product.productId "
                        + "GROUP BY p.productId, p.name, p.image, p.price " + "HAVING SUM(pv.quantity) <= :threshold")
        Page<ProductLowStockDto> findProductsLowStock(@Param("threshold") Integer threshold, Pageable pageable);

        // Lấy top 6 sản phẩm có giá cao nhất và có status = true
        @Query("SELECT p FROM Product p WHERE p.status = true ORDER BY p.price DESC")
        Page<Product> findTop6ByOrderByPriceDesc(Pageable pageable);

        @Query("SELECT p FROM Product p WHERE p.price <= :price AND p.category.categoryId = :categoryId")
        List<Product> findByPriceLessThanEqualAndCategoryId(@Param("price") BigDecimal price,
                        @Param("categoryId") Integer categoryId);

        @Query("SELECT p FROM Product p WHERE p.status = true")
        List<Product> findByStatusTrue();

        @Query("SELECT p FROM Product p WHERE p.discount.discountId = :discountId AND p.status = true")
        List<Product> findDiscountedProductsByDiscountId(@Param("discountId") Integer discountId);

        @Query("SELECT p FROM Product p WHERE p.status = true")
        Page<Product> findByStatusTrue(Pageable pageable);
            
}
