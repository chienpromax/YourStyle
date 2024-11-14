package yourstyle.com.shope.controller.site.products;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductImage;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Review;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.ProductImageService;
import yourstyle.com.shope.service.ProductService;
import yourstyle.com.shope.service.ProductVariantService;
import yourstyle.com.shope.service.ReviewService;

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

    @GetMapping("yourstyle/product/detail/{productId}")
    public String productDetail(@PathVariable("productId") Integer productId,
            @RequestParam("page") Optional<Integer> page, Model model) {
        Optional<Product> productOptional = productService.findById(productId);

        // Kiểm tra nếu sản phẩm tồn tại
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            // Lấy danh sách biến thể của sản phẩm
            List<ProductVariant> productVariants = productVariantService.findByProductId(product.getProductId());

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
            model.addAttribute("productVariants", productVariants);
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
}
