package yourstyle.com.shope.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import yourstyle.com.shope.model.Review;

public interface ReviewService {

    Page<Review> findByProductId(Integer productId, Pageable pageable);

    Review save(Review review);

}
