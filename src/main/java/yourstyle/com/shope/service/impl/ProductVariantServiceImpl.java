package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import groovyjarjarantlr4.v4.parse.ANTLRParser.ruleEntry_return;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Size;
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

	@Override
	public List<ProductVariant> findByProductNameContaining(String name) {
		return productVariantRepository.findByProductNameContaining(name);
	}

	@Override
	public List<ProductVariant> findByProductVariantId(Integer productVariantId) {
		return productVariantRepository.findByProductVariantId(productVariantId);
	}

	@Override
	public List<ProductVariant> findBySize(Size size) {
		return productVariantRepository.findBySize(size);
	}

	@Override
	public List<ProductVariant> findByColor(Color color) {
		return productVariantRepository.findByColor(color);
	}

	@Override
	public List<ProductVariant> findByProduct(Product product) {
		return productVariantRepository.findByProduct(product);
	}

}
