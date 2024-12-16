package yourstyle.com.shope.controller.site;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.Discount;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.Slide;
import yourstyle.com.shope.repository.ProductRepository;
import yourstyle.com.shope.service.CategoryService;
import yourstyle.com.shope.service.ProductService;
import yourstyle.com.shope.service.SlideService;

import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/yourstyle")
public class HomeSiteController {
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;
	// new
	@Autowired
	private SlideService slideService;

	@Autowired
	private ProductRepository productRepository;

	@RequestMapping({ "/home", "/discount/{discountId}" })
	public String showHomePage(Model model, Authentication authentication,
			@PathVariable(value = "discountId", required = false) Integer discountId) {
		List<Category> parentCategories = categoryService.findParentCategories();
		model.addAttribute("parentCategories", parentCategories);

		String userName = (authentication != null && authentication.isAuthenticated()) ? authentication.getName()
				: null;
		model.addAttribute("userName", userName);

		// Lấy tất cả sản phẩm đang hoạt động
		List<Product> activeProducts = productService.findProductsWithStatusTrue();
		Collections.shuffle(activeProducts); // Trộn danh sách sản phẩm

		List<Product> randomProducts = activeProducts.stream()
				// .limit(12)
				.collect(Collectors.toList());
		model.addAttribute("products", randomProducts);

		// Lấy sản phẩm bán chạy
		List<Product> bestSellers = productService.getBestSellingProducts();
		model.addAttribute("bestSellers", bestSellers);

		// Lấy các sản phẩm đang hoạt động có mã giảm giá và còn hạn
		List<Product> discountedProducts = (discountId != null)
				? productService.getProductsByDiscountId(discountId)
				: productService.getDiscountedProducts();
		model.addAttribute("discountedProducts", discountedProducts);

		// Lấy danh sách tên giảm giá không trùng lặp
		List<String> uniqueDiscountNames = discountedProducts.stream()
				.map(product -> product.getDiscount().getDiscountName()) // Lấy tên giảm giá
				.distinct() // Loại bỏ trùng lặp
				.collect(Collectors.toList());
		model.addAttribute("uniqueDiscountNames", uniqueDiscountNames);

		// Lấy danh sách các slide và xử lý imagePaths
		List<Slide> slides = slideService.getAllSlides();
		for (Slide slide : slides) {
			String imagePaths = slide.getImagePaths();
			if (imagePaths != null) {
				String[] imagePathsArray = imagePaths.split(",");
				slide.setImagePathsArray(imagePathsArray);
			} else {
				slide.setImagePathsArray(new String[0]);
			}
		}
		model.addAttribute("slides", slides);

		List<Product> topExpensiveProducts = productService.getTop6ExpensiveProducts();
		model.addAttribute("topDeals", topExpensiveProducts);

		return "site/pages/home";
	}
}