package yourstyle.com.shope.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import yourstyle.com.shope.model.Review;

public interface ReviewService {

    Page<Review> findByProductId(Integer productId, Pageable pageable);

    Review save(Review review);

    // Lấy tất cả đánh giá
    List<Review> getAllReviews();

    // Lấy chi tiết một đánh giá
    Review getReviewById(Integer id);

    // Xóa đánh giá
    boolean deleteReview(Integer id);

    // Thêm phương thức xóa nhiều đánh giá
    void deleteReviewsByIds(List<Integer> reviewIds);
    
    Review findById(Integer reviewId);
    //pthuc new
    boolean deleteMultipleReviews(List<Integer> reviewIds);
}
