package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;

import yourstyle.com.shope.model.ProductVariant;

public interface ProductVariantService {

	ProductVariant getById(Integer id);

	void deleteById(Integer id);

	ProductVariant getOne(Integer id);

	Optional<ProductVariant> findById(Integer id);

	List<ProductVariant> findAll();

	<S extends ProductVariant> S save(S entity);

}
