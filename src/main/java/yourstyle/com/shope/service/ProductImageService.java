package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import yourstyle.com.shope.model.ProductImage;

public interface ProductImageService {

    void deleteById(Integer id);

    long count();

    Optional<ProductImage> findById(Integer id);

    List<ProductImage> findAll();

    Page<ProductImage> findAll(Pageable pageable);

    List<ProductImage> findAll(Sort sort);

    <S extends ProductImage> Optional<S> findOne(Example<S> example);

    <S extends ProductImage> S save(S entity);

    ProductImage update(ProductImage productImage);

    List<ProductImage> findByProductId(Integer productId);

    void saveProductImages(Integer productId, MultipartFile[] imageFiles);

}
