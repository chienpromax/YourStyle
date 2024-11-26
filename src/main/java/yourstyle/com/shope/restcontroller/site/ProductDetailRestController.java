package yourstyle.com.shope.restcontroller.site;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.Review;
import yourstyle.com.shope.model.ReviewImage;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.ProductService;
import yourstyle.com.shope.service.ReviewImageService;
import yourstyle.com.shope.service.ReviewService;
import yourstyle.com.shope.utils.UploadUtils;
import yourstyle.com.shope.validation.site.PageDto;
import yourstyle.com.shope.validation.site.ReviewDto;

@CrossOrigin("*")
@RestController
public class ProductDetailRestController {
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ReviewImageService reviewImageService;

    @GetMapping("/reviews/{productId}")
    @Transactional
    public ResponseEntity<PageDto<ReviewDto>> getProductReviews(@PathVariable("productId") Integer productId,
            @RequestParam("page") Optional<Integer> page) {
        int currentPage = page.orElse(0);
        Pageable pageable = PageRequest.of(currentPage, 5);

        Page<Review> reviewsPage = reviewService.findByProductId(productId, pageable);
        Page<ReviewDto> reviewDtos = reviewsPage.map(review -> {
            ReviewDto reviewDto = new ReviewDto();
            reviewDto.setReviewId(review.getReviewId());
            reviewDto.setRating(review.getRating());
            reviewDto.setComment(review.getComment());
            reviewDto.setCreateAt(review.getCreateAt());
            reviewDto.setUpdateAt(review.getUpdateAt());
            reviewDto.setCustomer(review.getCustomer());
            reviewDto.setProduct(review.getProduct());
            List<String> imageUrls = review.getReviewImages().stream()
                    .map(ReviewImage::getImageUrl)
                    .collect(Collectors.toList());
            reviewDto.setImages(imageUrls);
            return reviewDto;
        });

        PageDto<ReviewDto> pageDto = new PageDto<>(reviewDtos);
        return ResponseEntity.ok(pageDto);
    }

    @GetMapping("/reviews/getCustomer")
    public Customer getCustomer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountService.findByUsername(username);

        Customer customer = customerService.findByAccountId(account.getAccountId());
        return customer;
    }

    // Thêm đánh giá cho sản phẩm
    @PostMapping("/reviews/{productId}")
    public ResponseEntity<Review> submitReview(
            @PathVariable("productId") Integer productId,
            @RequestPart("review") String reviewJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        Review review = mapper.readValue(reviewJson, Review.class);

        Optional<Product> product = productService.findById(productId);
        review.setProduct(product.get());

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountService.findByUsername(username);

        Customer customer = customerService.findByAccountId(account.getAccountId());
        review.setCustomer(customer);

        // Save Review
        Review savedReview = reviewService.save(review);

        if (images != null) {
            for (MultipartFile image : images) {
                String uploadDir = "src/main/resources/static/uploads/reviewImages";
                String originalFilename = image.getOriginalFilename();
                String imageUrl = UploadUtils.saveFile(uploadDir, originalFilename, image);

                ReviewImage reviewImage = new ReviewImage();
                reviewImage.setImageUrl(imageUrl);
                reviewImage.setReview(savedReview);
                // Save image to database
                reviewImageService.save(reviewImage);
            }
        }

        return ResponseEntity.ok(savedReview);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable("reviewId") Integer reviewId) {
        Optional<Review> reviewOpt = reviewService.findById(reviewId);
        if (reviewOpt.isPresent()) {
            reviewImageService.deleteByReviewId(reviewId);
            reviewService.deleteById(reviewId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
