package yourstyle.com.shope.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import yourstyle.com.shope.model.ProductImage;

@Controller
public class ProductImageController {
    @GetMapping("/admin/productimages/saveOrUpdate")
    public String showProductImageForm(Model model) {
        model.addAttribute("productImage", new ProductImage());
        return "admin/products/addOrEdit";
    }

    @PostMapping("/admin/productimages/saveOrUpdate")
    public String saveOrUpdateProductImage(@ModelAttribute("productImage") ProductImage ProductImage) {

        return "redirect:/admin/productimages";
    }
}
