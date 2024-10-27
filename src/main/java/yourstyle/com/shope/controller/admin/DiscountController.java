package yourstyle.com.shope.controller.admin;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import yourstyle.com.shope.model.Discount;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.service.DiscountService;
import yourstyle.com.shope.service.ProductService;

@Controller
@RequestMapping("/admin/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @Autowired
    private ProductService productService;

    @GetMapping("/add")
    public String showDiscountForm(@RequestParam("productId") Integer productId, Model model) {
        model.addAttribute("discount", new Discount());
        model.addAttribute("productId", productId);
        return "admin/products/addOrEdit";
    }

    @PostMapping("/save")
    public String saveDiscount(@ModelAttribute Discount discount, @RequestParam Integer productId, RedirectAttributes redirectAttributes) {
        Optional<Product> optionalProduct = productService.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            discount.setProduct(product);
            discountService.save(discount);
            redirectAttributes.addFlashAttribute("message", "Mã giảm giá đã được lưu thành công!");
        } else {
            redirectAttributes.addFlashAttribute("message", "Sản phẩm không tồn tại!");
        }
        return "redirect:/admin/products/edit/" + productId;
    }
    
}
