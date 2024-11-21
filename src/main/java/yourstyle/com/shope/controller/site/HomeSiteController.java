package yourstyle.com.shope.controller.site;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.Slide;
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

	@RequestMapping({"/home", "/discount/{discountId}"})
	public String showHomePage(Model model, Authentication authentication, @PathVariable(value = "discountId", required = false) Integer discountId) {
		List<Category> parentCategories = categoryService.findParentCategories();

		model.addAttribute("parentCategories", parentCategories);

		String userName = (authentication != null && authentication.isAuthenticated()) ? authentication.getName()
				: null;
		model.addAttribute("userName", userName);

		List<Product> products = productService.getAllProducts();

		model.addAttribute("products", products);
    
        // Lấy sản phẩm bán chạy
        List<Product> bestSellers = productService.getBestSellingProducts();
        model.addAttribute("bestSellers", bestSellers);
     
        // Lấy sản phẩm giảm giá theo discountId nếu có
        List<Product> discountedProducts = (discountId != null) ? 
                                            productService.getProductsByDiscountId(discountId) : 
                                            productService.getDiscountedProducts();
        model.addAttribute("discountedProducts", discountedProducts);

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

		return "site/pages/home";
	}
}