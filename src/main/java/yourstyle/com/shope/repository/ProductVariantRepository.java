package yourstyle.com.shope.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Size;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.name LIKE %?1%")
    List<ProductVariant> findByProductNameContaining(String name);

    List<ProductVariant> findByProductVariantId(Integer productVariantId);

    List<ProductVariant> findBySize(Size size);

    List<ProductVariant> findByColor(Color color);

    List<ProductVariant> findByProduct(Product product);
}
