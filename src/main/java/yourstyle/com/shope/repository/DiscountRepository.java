package yourstyle.com.shope.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer>{
    
    List<Discount> findByProduct_ProductId(Integer productId);

}
