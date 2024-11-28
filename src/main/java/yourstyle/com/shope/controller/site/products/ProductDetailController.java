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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductImage;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Review;
import yourstyle.com.shope.model.Size;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.service.EmailService;
import yourstyle.com.shope.service.OrderService;
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
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmailService emailService;

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

    @GetMapping("yourstyle/product/detail/{productId}")
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

    @PostMapping("/yourstyle/product/share")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> shareProduct(
            @RequestParam("recipientEmail") String recipientEmail,
            @RequestParam("senderEmail") String senderEmail,
            @RequestParam("productId") Integer productId,
            Authentication authentication) {
    
        Map<String, Object> response = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>(); // Lưu lỗi từng trường
    
        // Kiểm tra đăng nhập
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("errorMessage", "Bạn cần đăng nhập để thực hiện hành động này.");
            response.put("redirectUrl", "/yourstyle/accounts/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    
        // Kiểm tra sản phẩm tồn tại
        Optional<Product> productOptional = productService.findById(productId);
        if (productOptional.isEmpty()) {
            fieldErrors.put("productId", "Sản phẩm không tồn tại.");
        }
    
        // Kiểm tra email người nhận hợp lệ
        if (!recipientEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            fieldErrors.put("recipientEmail", "Email người nhận không hợp lệ.");
        }
    
        // Nếu có lỗi thì trả về lỗi
        if (!fieldErrors.isEmpty()) {
            response.put("success", false);
            response.put("fieldErrors", fieldErrors); // Trả về lỗi theo từng trường
            return ResponseEntity.badRequest().body(response);
        }
    
        try {
            // Gửi email
            Product product = productOptional.get();
            emailService.sendProductShareEmail(senderEmail, recipientEmail, product);
            response.put("success", true);
            response.put("message", "Chia sẻ sản phẩm thành công!");
            response.put("redirectUrl", "/yourstyle/product/detail/" + productId); // URL chuyển hướng
        } catch (Exception e) {
            response.put("success", false);
            response.put("errorMessage", "Đã xảy ra lỗi khi gửi email: " + e.getMessage());
        }
    
        return ResponseEntity.ok(response);
    }
    
}
