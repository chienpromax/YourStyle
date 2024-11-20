package yourstyle.com.shope.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public Optional<Review> findById(Integer reviewId) {
        return reviewRepository.findById(reviewId);
    }

    @Override
    public void deleteById(Integer reviewId) {
        reviewRepository.deleteById(reviewId);
    }

}
