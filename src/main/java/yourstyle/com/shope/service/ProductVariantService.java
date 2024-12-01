package yourstyle.com.shope.service;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Size;

public interface ProductVariantService {

    void deleteById(Integer id);

    long count();

    Optional<ProductVariant> findById(Integer id);

    List<ProductVariant> findAll();

    Page<ProductVariant> findAll(Pageable pageable);

    List<ProductVariant> findAll(Sort sort);

    <S extends ProductVariant> Optional<S> findOne(Example<S> example);

    <S extends ProductVariant> S save(S entity);

    ProductVariant update(ProductVariant productVariant);

    boolean existsById(Integer id);

    List<ProductVariant> getAllProductVariantsOrderedByPrice();

    Page<ProductVariant> searchByProductName(String productName, Pageable pageable);

    List<ProductVariant> findByProductId(Integer productId);

    List<ProductVariant> findByProductVariantId(Integer productVariantId);

    List<ProductVariant> findByProductNameContaining(String name);

    List<ProductVariant> findBySize(Size size);

    List<ProductVariant> findByColor(Color color);

    List<ProductVariant> findByProduct(Product product);

    // lọc sản phẩm
    List<ProductVariant> findBySize(Size size, Integer categoryId);

    List<ProductVariant> findByColor(Color color, Integer categoryId);

    List<ProductVariant> findBySizeAndColor(Size size, Color color, Integer categoryId);
}
