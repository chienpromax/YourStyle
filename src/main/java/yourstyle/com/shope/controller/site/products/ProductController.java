package yourstyle.com.shope.controller.site.products;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.service.ProductService;

@Controller
@RequestMapping("/allproduct")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String getAllProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;

        if (categoryId != null) {
            productPage = productService.findByCategory_CategoryId(categoryId, pageable);
        } else {
            productPage = productService.findAll(pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        return "/site/products/allproduct";
    }
}

