package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.repository.ProductVariantRepository;
import yourstyle.com.shope.service.ProductVariantService;

@Service
public class ProductVariantServiceImpl implements ProductVariantService {
    @Autowired
    ProductVariantRepository productVariantRepository;

	public ProductVariantServiceImpl(ProductVariantRepository productVariantRepository) {
		this.productVariantRepository = productVariantRepository;
	}

	@Override
	public <S extends ProductVariant> S save(S entity) {
		return productVariantRepository.save(entity);
	}

	@Override
	public List<ProductVariant> findAll() {
		return productVariantRepository.findAll();
	}

	@Override
	public Optional<ProductVariant> findById(Integer id) {
		return productVariantRepository.findById(id);
	}

	@Override
	public ProductVariant getOne(Integer id) {
		return productVariantRepository.getOne(id);
	}

	@Override
	public void deleteById(Integer id) {
		productVariantRepository.deleteById(id);
	}

	@Override
	public ProductVariant getById(Integer id) {
		return productVariantRepository.getById(id);
	}

    
    
}
