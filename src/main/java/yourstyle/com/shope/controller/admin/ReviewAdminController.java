package yourstyle.com.shope.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import yourstyle.com.shope.model.Review;
import yourstyle.com.shope.service.ReviewService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

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
	
	 // Xóa nhiều đánh giá
    @PostMapping("/deleteMultiple")
    public ResponseEntity<Map<String, String>> deleteMultipleReviews(@RequestParam("reviewIds") String reviewIdListStr) {
        Map<String, String> response = new HashMap<>();
        
        System.out.println("Received reviewIds: " + reviewIdListStr);  // Debug: In ra dữ liệu nhận được từ client
        
        try {
            if (reviewIdListStr == null || reviewIdListStr.trim().isEmpty()) {
                response.put("message", "Danh sách reviewId không hợp lệ.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Chuyển chuỗi reviewIds thành danh sách reviewId
            List<String> reviewIdsString = Arrays.asList(reviewIdListStr.split(","));
            System.out.println("Parsed reviewIds: " + String.join(",", reviewIdsString));  // Debug: In ra các reviewId đã tách ra
            
            // Kiểm tra từng reviewId có hợp lệ không
            for (String reviewId : reviewIdsString) {
                try {
                    Integer.parseInt(reviewId);  
                    System.out.println("Valid ReviewId: " + reviewId);  // Debug: Kiểm tra reviewId hợp lệ
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ReviewId: " + reviewId);  // Debug: In ra reviewId không hợp lệ
                    response.put("message", "Định dạng reviewId không hợp lệ.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); 
                }
            }

            
            List<Integer> reviewIds = reviewIdsString.stream()
                                                     .map(Integer::parseInt)  
                                                     .collect(Collectors.toList());
            System.out.println("ReviewIds to delete: " + reviewIds);  

            // Xóa các review
            boolean result = reviewService.deleteMultipleReviews(reviewIds);
            if (result) {
                response.put("message", "Xóa thành công.");
                return ResponseEntity.ok(response); 
            } else {
                response.put("message", "Xóa không thành công.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); 
            }
        } catch (Exception e) {
            e.printStackTrace();  // In ra chi tiết lỗi
            response.put("message", "Đã xảy ra lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); 
        }
    }

}
