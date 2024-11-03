package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;

import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Size;

public interface ProductVariantService {

	ProductVariant getById(Integer id);

	void deleteById(Integer id);

	ProductVariant getOne(Integer id);

	Optional<ProductVariant> findById(Integer id);

	List<ProductVariant> findAll();

	<S extends ProductVariant> S save(S entity);

	List<ProductVariant> findByProductNameContaining(String name);

	List<ProductVariant> findByProductVariantId(Integer productVariantId);

	List<ProductVariant> findBySize(Size size);

	List<ProductVariant> findByColor(Color color);

	List<ProductVariant> findByProduct(Product product);
}
