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
            @RequestParam(required = false) String sort,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        List<Product> products;
    
        if ("best-sellers".equals(sort)) {
            // Lấy danh sách sản phẩm bán chạy nhất
            products = productService.getBestSellingProducts();
        } else if ("discount".equals(sort)) {
            // Lấy danh sách sản phẩm có giảm giá
            products = productService.getDiscountedProducts();
        } else if (categoryId != null) {
            // Lọc theo danh mục nếu categoryId không null
            Page<Product> productPage = productService.findByCategory_CategoryId(categoryId, pageable);
            products = productPage.getContent();
        } else {
            // Lấy tất cả sản phẩm nếu không có điều kiện sắp xếp
            Page<Product> productPage = productService.findAll(pageable);
            products = productPage.getContent();
        }
    
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (products.size() + size - 1) / size);
        model.addAttribute("sort", sort);
    
        return "/site/products/allproduct";
    }
    
    

}

