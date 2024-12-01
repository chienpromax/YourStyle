package yourstyle.com.shope.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Example;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.Product;

public interface ProductService {

    void deleteById(Integer id);

    long count();

    Optional<Product> findById(Integer id);

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    List<Product> findAll(Sort sort);

    <S extends Product> Optional<S> findOne(Example<S> example);

    <S extends Product> S save(S entity);

    Product update(Product product);

    Page<Product> searchByName(String name, Pageable pageable);

    List<Product> findByCategoryId(Integer categoryId);

    List<Product> getAllProducts();

    List<Product> findSimilarProducts(Integer categoryId, Integer productId);

    List<Product> findByCategory_CategoryId(Integer categoryId); // Phương thức tìm theo categoryId

    Page<Product> findByCategory_CategoryId(Integer categoryId, Pageable pageable);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> getBestSellingProducts();

    List<Product> getDiscountedProducts();

    List<Product> getProductsByDiscountId(Integer discountId);

    List<Product> findByCategory(Category category);    
    
    //6 sp cao price 1
    Page<Product> getTop6Products(Pageable pageable);

    List<Product> findByPriceLessThanEqualAndCategoryId(BigDecimal price, Integer categoryId);
}
