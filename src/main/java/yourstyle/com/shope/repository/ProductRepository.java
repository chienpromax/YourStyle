package yourstyle.com.shope.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Page<Product> findByNameContaining(String name, Pageable pageable);

    List<Product> findByCategory_CategoryId(Integer categoryId);

    @Query("SELECT p FROM Product p WHERE p.category.categoryId = ?1 AND p.productId != ?2")
    List<Product> findSimilarProducts(Integer categoryId, Integer productId);
}
