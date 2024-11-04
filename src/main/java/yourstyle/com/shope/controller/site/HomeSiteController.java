package yourstyle.com.shope.controller.site;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.service.CategoryService;
import yourstyle.com.shope.service.ProductService;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/yourstyle")
public class HomeSiteController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @RequestMapping("/home")

    public String showHomePage(Model model, Authentication authentication) {
        List<Category> parentCategories = categoryService.findParentCategories();

        model.addAttribute("parentCategories", parentCategories);

        String userName = (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : null;
        model.addAttribute("userName", userName);

        List<Product> products = productService.getAllProducts();

        model.addAttribute("products", products);
        return "site/pages/home"; 
    }
}

