package yourstyle.com.shope.service.impl;

import java.util.Optional;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductImage;
import yourstyle.com.shope.repository.ProductImageRepository;
import yourstyle.com.shope.repository.ProductRepository;
import yourstyle.com.shope.service.ProductImageService;
import yourstyle.com.shope.utils.UploadUtils;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    @Autowired
    ProductImageRepository productImageRepository;
    @Autowired
    ProductRepository productRepository;

    @Autowired
    public ProductImageServiceImpl(ProductImageRepository productImageRepository) {
        this.productImageRepository = productImageRepository;
    }

    @Override
    public ProductImage update(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }

    @Override
    public <S extends ProductImage> S save(S entity) {
        return productImageRepository.save(entity);
    }

    @Override
    public <S extends ProductImage> Optional<S> findOne(Example<S> example) {
        return productImageRepository.findOne(example);
    }

    @Override
    public Page<ProductImage> findAll(Pageable pageable) {
        return productImageRepository.findAll(pageable);
    }

    @Override
    public List<ProductImage> findAll(Sort sort) {
        return productImageRepository.findAll(sort);
    }

    @Override
    public List<ProductImage> findAll() {
        return productImageRepository.findAll();
    }

    @Override
    public Optional<ProductImage> findById(Integer id) {
        return productImageRepository.findById(id);
    }

    @Override
    public long count() {
        return productImageRepository.count();
    }

    @Override
    public void deleteById(Integer id) {
        productImageRepository.deleteById(id);
    }

    @Override
    public List<ProductImage> findByProductId(Integer productId) {
        return productImageRepository.findByProduct_ProductId(productId); 
    }

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @Override
    public void saveProductImages(Integer productId, MultipartFile[] imageFiles) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    
        for (MultipartFile file : imageFiles) {
            if (!file.isEmpty()) {
                try {
                    String originalFileName = file.getOriginalFilename();
                    UploadUtils.saveFile(UPLOAD_DIR, originalFileName, file);
    
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(originalFileName);
                    productImage.setProduct(product);
                    productImageRepository.save(productImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
