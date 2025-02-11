package yourstyle.com.shope.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.repository.ProductRepository;
import yourstyle.com.shope.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	ProductRepository productRepository;

	@Override
    public List<Product> getProductsByDiscountId(Integer discountId) {
        return productRepository.findDiscountedProductsByDiscountId(discountId);
    }

    @Override
    public List<Product> getDiscountedProducts() {
        return productRepository.findValidDiscountedProducts(); // Chỉ lấy mã giảm giá còn hạn
    }

	
	@Override
	public List<Product> getBestSellingProducts() {
		List<Object[]> results = productRepository.findBestSellingProducts();
		return results.stream().map(result -> {
			Product product = new Product();
			product.setProductId((Integer) result[0]);
			product.setName((String) result[1]);
			product.setPrice((BigDecimal) result[2]);
			product.setImage((String) result[3]);
			product.setTotalQuantity(((Number) result[4]).intValue());
			product.setStatus(true); // Có thể ánh xạ status nếu cần
			return product;
		}).collect(Collectors.toList());
	}

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

	@Override
	public List<Product> findByCategory(Category category) {
		return productRepository.findByCategory(category);
	}

	// Phương thức lấy top 6 sản phẩm cao giá nhất
	@Override
	public List<Product> getTop6ExpensiveProducts() {
		Pageable pageable = PageRequest.of(0, 6); // Trang 0, số lượng 6
		return productRepository.findTop6ByOrderByPriceDesc(pageable).getContent();
	}

	@Override
	public List<Product> findByPriceLessThanEqualAndCategoryId(BigDecimal price, Integer categoryId) {
		return productRepository.findByPriceLessThanEqualAndCategoryId(price, categoryId);
	}

	@Override
	public List<Product> findProductsWithStatusTrue() {
		return productRepository.findByStatusTrue();
	}

	@Override
	public Page<Product> findByStatusTrue(Pageable pageable) {
		return productRepository.findByStatusTrue(pageable);
	}

	@Override
	public List<Product> getActiveDiscountedProducts() {
		List<Product> products = productRepository.findAll();
		return products.stream()
				.filter(product -> product.getDiscount() != null &&
						product.getDiscount().getEndDate().isAfter(LocalDateTime.now()))
				.collect(Collectors.toList());
	}

}
