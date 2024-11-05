package yourstyle.com.shope.controller.site.products;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductImage;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Review;
import yourstyle.com.shope.model.Size;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.ProductImageService;
import yourstyle.com.shope.service.ProductService;
import yourstyle.com.shope.service.ProductVariantService;
import yourstyle.com.shope.service.ReviewService;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProductDetailController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductVariantService productVariantService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/product/detail/{productId}")
    public String productDetail(@PathVariable("productId") Integer productId,
            @RequestParam("page") Optional<Integer> page, Model model) {
        Optional<Product> productOptional = productService.findById(productId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            List<ProductVariant> productVariants = productVariantService.findByProductId(product.getProductId());
            Set<Color> uniqueColors = productVariants.stream()
                    .map(ProductVariant::getColor)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            Set<Size> uniqueSizes = productVariants.stream()
                    .map(ProductVariant::getSize)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            List<ProductImage> productImages = productImageService.findByProductId(productId);

            // Lấy danh sách sản phẩm tương tự
            List<Product> similarProducts = productService.findSimilarProducts(product.getCategory().getCategoryId(),
                    product.getProductId());
            Collections.shuffle(similarProducts);

            // Giới hạn danh sách sản phẩm tương tự tối đa 4 sản phẩm
            if (similarProducts.size() > 4) {
                similarProducts = similarProducts.subList(0, 4);
            }

            int currentPage = page.orElse(0); // trang hiện tại
            // Phân trang cho danh sách đánh giá
            Pageable pageable = PageRequest.of(currentPage, 5); // 5 đánh giá mỗi trang
            Page<Review> reviewsPage = reviewService.findByProductId(product.getProductId(), pageable);

            // Tính toán rating trung bình
            List<Review> reviews = reviewsPage.getContent();
            double averageRating = reviews.stream()
                    .filter(Objects::nonNull) // Loại bỏ các phần tử null
                    .mapToInt(review -> review.getRating() != null ? review.getRating() : 0) // Xử lý rating null
                    .average()
                    .orElse(0.0); // Nếu không có đánh giá, đặt rating trung bình là 0

            model.addAttribute("product", product);
            model.addAttribute("uniqueColors", uniqueColors);
            model.addAttribute("uniqueSizes", uniqueSizes);
            model.addAttribute("similarProducts", similarProducts);
            model.addAttribute("reviewsPage", reviewsPage); // Thêm danh sách đánh giá vào model
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("productImages", productImages);

            return "site/products/productdetail";
        } else {
            model.addAttribute("error", "Sản phẩm không tồn tại");
            return "redirect:/yourstyle/home";
        }
    }

    @PostMapping("/product/review")
    public String reviewProduct(@RequestParam String comment, Authentication authentication,
            @RequestParam Integer productId) {
        Review review = new Review();
        review.setComment(comment);

        Optional<Customer> customer = customerService.findById(13);
        Optional<Product> product = productService.findById(productId);

        review.setProduct(product.get());
        review.setCustomer(customer.get()); // Đảm bảo rằng bạn đã có id của sản phẩm
        reviewService.save(review); // Giả định rằng bạn đã có phương thức save trong ReviewService
        return "redirect:/product/detail/" + productId; // Chuyển hướng về trang chi tiết sản phẩm
    }

}
