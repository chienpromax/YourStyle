package yourstyle.com.shope.controller.site.products;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.service.ProductService;

@Controller
public class SearchProductController {
@Autowired
ProductService productService;

@GetMapping("/search")
public String searchProductByName(@RequestParam("name") String name, Model model) {
    List<Product> products = productService.findByNameContainingIgnoreCase(name);
    model.addAttribute("products", products);
    model.addAttribute("searchTerm", name);
    return "site/products/allproduct"; 
}

}
