package yourstyle.com.shope.controller.site.products;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductImage;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Review;
import yourstyle.com.shope.model.Size;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.repository.OrderDetailRepository;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderService;
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
    private CustomerService customerService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping("/yourstyle/carts/addtocart")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(@RequestParam("productVariantId") Integer productVariantId,
            @RequestParam("colorId") Integer colorId,
            @RequestParam("sizeId") Integer sizeId,
            @RequestParam("quantity") Integer quantity,
            Authentication authentication) {
    
        Map<String, Object> response = new HashMap<>();
    
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("errorMessage", "Bạn cần đăng nhập để thực hiện hành động này.");
            response.put("redirectUrl", "/yourstyle/accounts/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer accountId = userDetails.getAccountId();
        Customer customer = customerRepository.findByAccount_AccountId(accountId);
        if (customer == null) {
            response.put("success", false);
            response.put("errorMessage", "Không tìm thấy khách hàng.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            orderService.addProductToCart(customer.getCustomerId(), productVariantId, colorId, sizeId, quantity);
            response.put("success", true);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("errorMessage", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/detail/{productId}")
    public String productDetail(@PathVariable("productId") Integer productId,
            @RequestParam("page") Optional<Integer> page, Model model) {
        Optional<Product> productOptional = productService.findById(productId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            List<ProductVariant> productVariants = productVariantService.findByProductId(product.getProductId());
            Integer selectedVariantId = productVariants.isEmpty() ? null : productVariants.get(0).getProductVariantId();

            // Lọc các màu sắc và kích thước duy nhất
            Set<Color> uniqueColors = new HashSet<>();
            Set<Size> uniqueSizes = new HashSet<>();
            for (ProductVariant variant : productVariants) {
                uniqueColors.add(variant.getColor());
                uniqueSizes.add(variant.getSize());
            }

            model.addAttribute("uniqueColors", uniqueColors);
            model.addAttribute("uniqueSizes", uniqueSizes);
            model.addAttribute("selectedVariantId", selectedVariantId);
            model.addAttribute("productVariants", productVariants);

            // Các xử lý khác
            List<ProductImage> productImages = productImageService.findByProductId(productId);
            List<Product> similarProducts = productService.findSimilarProducts(product.getCategory().getCategoryId(),
                    product.getProductId());
            Collections.shuffle(similarProducts);
            if (similarProducts.size() > 4) {
                similarProducts = similarProducts.subList(0, 4);
            }

            int currentPage = page.orElse(0);
            Pageable pageable = PageRequest.of(currentPage, 5);
            Page<Review> reviewsPage = reviewService.findByProductId(product.getProductId(), pageable);
            List<Review> reviews = reviewsPage.getContent();
            double averageRating = reviews.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(review -> review.getRating() != null ? review.getRating() : 0)
                    .average()
                    .orElse(0.0);

            model.addAttribute("product", product);
            model.addAttribute("similarProducts", similarProducts);
            model.addAttribute("reviewsPage", reviewsPage);
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
