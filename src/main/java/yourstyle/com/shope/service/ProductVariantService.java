package yourstyle.com.shope.service;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import yourstyle.com.shope.model.ProductVariant;


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

}
