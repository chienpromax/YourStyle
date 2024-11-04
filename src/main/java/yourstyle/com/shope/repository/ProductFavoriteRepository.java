package yourstyle.com.shope.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.ProductFavorite;

@Repository
public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, Integer> {
	boolean existsByCustomerIdAndProductId(int customerId, int productId);

	Optional<ProductFavorite> findByCustomerIdAndProductId(int customerId, int productId);

	List<Integer> findProductIdsByCustomerId(int customerId);

	List<ProductFavorite> findProductFavoritesByCustomerId(int customerId);

	
}
