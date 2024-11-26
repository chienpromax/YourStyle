package yourstyle.com.shope.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import yourstyle.com.shope.model.ReviewImage;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM ReviewImage ri WHERE ri.review.reviewId = :reviewId")
    void deleteByReviewId(Integer reviewId);

}
