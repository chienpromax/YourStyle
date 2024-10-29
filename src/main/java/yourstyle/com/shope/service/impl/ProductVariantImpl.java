package yourstyle.com.shope.service.impl;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.repository.ProductVariantRepository;
import yourstyle.com.shope.service.ProductVariantService;

@Service
public class ProductVariantImpl implements ProductVariantService {

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Override
    public void deleteById(Integer id) {
        productVariantRepository.deleteById(id);
    }

    @Override
    public long count() {
        return productVariantRepository.count();
    }

    @Override
    public Optional<ProductVariant> findById(Integer id) {
        return productVariantRepository.findById(id);
    }

    @Override
    public List<ProductVariant> findAll() {
        return productVariantRepository.findAll();
    }

    @Override
    public Page<ProductVariant> findAll(Pageable pageable) {
        return productVariantRepository.findAll(pageable);
    }

    @Override
    public List<ProductVariant> findAll(Sort sort) {
        return productVariantRepository.findAll(sort);
    }

    @Override
    public <S extends ProductVariant> Optional<S> findOne(Example<S> example) {
        return productVariantRepository.findOne(example);
    }

    @Override
    public <S extends ProductVariant> S save(S entity) {
        return productVariantRepository.save(entity);
    }

    @Override
    public ProductVariant update(ProductVariant productVariant) {
        return productVariantRepository.save(productVariant);
    }

    @Override
    public boolean existsById(Integer id) {
        return productVariantRepository.existsById(id);
    }
    
    @Override
    public List<ProductVariant> getAllProductVariantsOrderedByPrice() {
        return productVariantRepository.findAllOrderByProductPrice();
    }

    @Override
    public Page<ProductVariant> searchByProductName(String productName, Pageable pageable) {
        return productVariantRepository.findByProductNameContainingIgnoreCase(productName, pageable);
    }
}
