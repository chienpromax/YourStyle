package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.repository.ProductRepository;
import yourstyle.com.shope.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	ProductRepository productRepository;

	@Override
    public List<Product> findByNameContainingIgnoreCase(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

	public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public List<Product> findByCategory_CategoryId(Integer categoryId) {
		return productRepository.findByCategory_CategoryId(categoryId);
	}

	@Override
    public Page<Product> findByCategory_CategoryId(Integer categoryId, Pageable pageable) {
        return productRepository.findByCategory_CategoryId(categoryId, pageable);
    }
	@Override
	public Product update(Product product) {
		return productRepository.save(product);
	}

	@Override
	public <S extends Product> S save(S entity) {
		return productRepository.save(entity);
	}

	@Override
	public <S extends Product> Optional<S> findOne(Example<S> example) {
		return productRepository.findOne(example);
	}

	@Override
	public Page<Product> findAll(Pageable pageable) {
		return productRepository.findAll(pageable);
	}

	@Override
	public List<Product> findAll(Sort sort) {
		return productRepository.findAll(sort);
	}

	@Override
	public List<Product> findAll() {
		return productRepository.findAll();
	}

	@Override
	public Optional<Product> findById(Integer id) {
		return productRepository.findById(id);
	}

	@Override
	public long count() {
		return productRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		productRepository.deleteById(id);
	}

	@Override
	public Page<Product> searchByName(String name, Pageable pageable) {
		return productRepository.findByNameContaining(name, pageable);
	}

	@Override
	public List<Product> findByCategoryId(Integer categoryId) {
		return productRepository.findByCategory_CategoryId(categoryId);
	}

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public List<Product> findSimilarProducts(Integer categoryId, Integer productId) {
		return productRepository.findSimilarProducts(categoryId, productId);
	}

}
