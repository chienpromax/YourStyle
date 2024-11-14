package yourstyle.com.shope.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import yourstyle.com.shope.model.Review;
import yourstyle.com.shope.service.ReviewService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/reviews")
public class ReviewAdminController {

	@Autowired
	private ReviewService reviewService;

	// Hiển thị danh sách đánh giá
	@GetMapping
	public String showReviewList(Model model) {
		List<Review> reviews = reviewService.getAllReviews();

		model.addAttribute("reviews", reviews);
		return "admin/reviews/list";
	}

	// Xóa một đánh giá
	// Lấy thông tin đánh giá (dùng GET để thử nghiệm)
	@GetMapping("/delete/{reviewId}")
	public ResponseEntity<String> getReview(@PathVariable Integer reviewId) {
		System.out.println("Review ID được nhận: " + reviewId);
		boolean isDeleted = reviewService.deleteReview(reviewId);
		if (isDeleted) {
			return ResponseEntity.ok("Review đã được xóa thành công.");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy review.");
		}
	}



}
