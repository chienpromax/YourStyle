package yourstyle.com.shope.restcontroller.site;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.Review;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.ProductService;
import yourstyle.com.shope.service.ReviewService;

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

    @GetMapping("/reviews/{productId}")
    @Transactional
    public ResponseEntity<Page<Review>> getProductReviews(@PathVariable("productId") Integer productId,
            @RequestParam("page") Optional<Integer> page) {
        int currentPage = page.orElse(0); // Default to page 0 if not specified
        Pageable pageable = PageRequest.of(currentPage, 5); // 5 reviews per page
        Page<Review> reviewsPage = reviewService.findByProductId(productId, pageable);
        return ResponseEntity.ok(reviewsPage);
    }

    // Thêm đánh giá cho sản phẩm
    @PostMapping("/reviews/{productId}")
    @Transactional
    public ResponseEntity<Review> submitReview(@PathVariable("productId") Integer productId,
            @RequestBody Review review, @RequestParam(value = "image", required = false) MultipartFile image) {
        // Gán productId cho review
        Optional<Product> product = productService.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        review.setProduct(product.get());

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountService.findByUsername(username);

        Customer customer = customerService.findByAccountId(account.getAccountId());
        review.setCustomer(customer);

        // Lưu review vào cơ sở dữ liệu
        Review savedReview = reviewService.save(review);

        return ResponseEntity.ok(savedReview);
    }
}
