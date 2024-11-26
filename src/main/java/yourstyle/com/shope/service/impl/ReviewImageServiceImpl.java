package yourstyle.com.shope.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.ReviewImage;
import yourstyle.com.shope.repository.ReviewImageRepository;
import yourstyle.com.shope.service.ReviewImageService;

@Service
public class ReviewImageServiceImpl implements ReviewImageService {
    @Autowired
    ReviewImageRepository reviewImageRepository;

    @Override
    public void save(ReviewImage reviewImage) {
        reviewImageRepository.save(reviewImage);
    }

    @Override
    public List<ReviewImage> findAll() {
        return reviewImageRepository.findAll();
    }

    @Override
    public void deleteByReviewId(Integer reviewId) {
        reviewImageRepository.deleteByReviewId(reviewId);
    }
}
