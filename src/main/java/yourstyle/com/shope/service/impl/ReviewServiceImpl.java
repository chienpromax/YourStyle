package yourstyle.com.shope.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yourstyle.com.shope.model.Review;
import yourstyle.com.shope.repository.ReviewRepository;
import yourstyle.com.shope.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {
	@Autowired
	ReviewRepository reviewRepository;

	@Override
	public Page<Review> findByProductId(Integer productId, Pageable pageable) {
		return reviewRepository.findByProductId(productId, pageable);
	}

	@Override
	public Review save(Review review) {
		return reviewRepository.save(review);
	}

	@Override
	public List<Review> getAllReviews() {
		return reviewRepository.findAll();
	}

	@Override
	public Review getReviewById(Integer id) {
		Optional<Review> reviewOptional = reviewRepository.findById(id);
		return reviewOptional.orElse(null);
	}

	@Override
	public boolean deleteReview(Integer id) {
		if (reviewRepository.existsById(id)) {
			reviewRepository.deleteById(id);
			return true;
		}
		return false;
	}

	@Override
	public Review findById(Integer reviewId) {
		Optional<Review> review = reviewRepository.findById(reviewId);
		return review.orElse(null); // Return null if no review is found
	}


	@Override
	public void deleteReviewsByIds(List<Integer> reviewIds) {
		// Kiểm tra nếu danh sách rỗng
		if (reviewIds == null || reviewIds.isEmpty()) {
			System.out.println("Danh sách ID không hợp lệ.");
			return;
		}

		// Gọi phương thức xóa từ repository
		reviewRepository.deleteByReviewIdIn(reviewIds);
		System.out.println("Đã xóa các đánh giá có ID: " + reviewIds);
	}
	
	public boolean deleteMultipleReviews(List<Integer> reviewIds) {
	    try {
	        reviewRepository.deleteByReviewIdIn(reviewIds); // Gọi repository để xóa các đánh giá
	        return true; // Trả về true nếu xóa thành công
	    } catch (Exception e) {
	        e.printStackTrace(); // Ghi log lỗi để kiểm tra
	        return false; // Trả về false nếu có lỗi
	    }
	}


}
