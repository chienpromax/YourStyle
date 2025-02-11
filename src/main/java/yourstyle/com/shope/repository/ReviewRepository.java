package yourstyle.com.shope.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import yourstyle.com.shope.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE r.product.productId = ?1 ORDER BY r.createAt DESC")
    Page<Review> findByProductId(Integer productId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.reviewId IN :reviewIds")
    void deleteByReviewIdIn(@Param("reviewIds") List<Integer> reviewIds);

}
