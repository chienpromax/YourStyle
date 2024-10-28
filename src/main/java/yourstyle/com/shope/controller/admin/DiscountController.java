package yourstyle.com.shope.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Discount;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.Size;
import yourstyle.com.shope.service.ColorService;
import yourstyle.com.shope.service.DiscountService;
import yourstyle.com.shope.service.ProductService;
import yourstyle.com.shope.service.SizeService;

@Controller
@RequestMapping("/admin/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;
    @Autowired
    private ProductService productService;
    @Autowired
    ColorService colorService;
    @Autowired
    SizeService sizeService;

    @GetMapping("/add")
    public String showDiscountForm(@RequestParam("productId") Integer productId, Model model) {
        model.addAttribute("discount", new Discount());
        model.addAttribute("productId", productId);
        return "admin/products/addOrEdit";
    }

    @GetMapping("/edit/{discountId}")
    public String editDiscount(
            @PathVariable("discountId") Integer discountId,
            @RequestParam("productId") Integer productId,
            Model model) {

        Optional<Discount> discount = discountService.findById(discountId);
        Optional<Product> product = productService.findById(productId);

        if (discount.isPresent()) {
            Discount existingDiscount = discount.get();
            model.addAttribute("discount", existingDiscount);
            model.addAttribute("productId", productId);
            model.addAttribute("colors", colorService.findAll());
            model.addAttribute("sizes", sizeService.findAll());
            model.addAttribute("color", new Color());
            model.addAttribute("size", new Size());
        } else {
            model.addAttribute("discount", new Discount());
        }

        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("isEdit", true);
        } else {
            model.addAttribute("product", new Product());
            model.addAttribute("isEdit", false);
        }

        model.addAttribute("isEdit", true);
        return "admin/products/addOrEdit";
    }

    @PostMapping("/save")
    public String saveOrUpdateDiscount(@ModelAttribute Discount discount,
            @RequestParam(required = false) Integer productId,
            RedirectAttributes redirectAttributes) {
        if (productId != null) {
            Optional<Product> optionalProduct = productService.findById(productId);
            if (optionalProduct.isPresent()) {
                discount.setProduct(optionalProduct.get());
            } else {
                redirectAttributes.addFlashAttribute("message", "Sản phẩm không tồn tại!");
                return "redirect:/admin/products";
            }
        }

        discountService.save(discount);
        redirectAttributes.addFlashAttribute("message",
                discount.getDiscountId() != null ? "Mã giảm giá đã được cập nhật thành công!"
                        : "Mã giảm giá đã được lưu thành công!");

        // Chuyển hướng lại về trang chỉnh sửa sản phẩm
        return "redirect:/admin/products/edit/" + productId;
    }

    @GetMapping("/delete/{discountId}")
    public String deleteDiscount(@PathVariable("discountId") Integer discountId,
            RedirectAttributes redirectAttributes) {
        Optional<Discount> optionalDiscount = discountService.findById(discountId);
        if (optionalDiscount.isPresent()) {
            discountService.deleteById(discountId);
            redirectAttributes.addFlashAttribute("message", "Đã xóa mã giảm giá thành công!");
        } else {
            redirectAttributes.addFlashAttribute("message", "Mã giảm giá không tồn tại!");
        }
        return "redirect:/admin/products";
    }
}
