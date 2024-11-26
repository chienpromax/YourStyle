package yourstyle.com.shope.service;

import java.util.List;

import yourstyle.com.shope.model.ReviewImage;

public interface ReviewImageService {

    void save(ReviewImage reviewImage);

    List<ReviewImage> findAll();

    void deleteByReviewId(Integer reviewId);

}
