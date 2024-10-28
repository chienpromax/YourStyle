package yourstyle.com.shope.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
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
            model.addAttribute("isEdit", true);
        } else {
            model.addAttribute("discount", new Discount());
            model.addAttribute("isEdit", false);
        }
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
        } else {
            model.addAttribute("product", new Product());
        }

        return "admin/products/addOrEdit";
    }

    @PostMapping("/save")
    public ModelAndView saveOrUpdateDiscount(@ModelAttribute Discount discount,
            @RequestParam(required = false) Integer productId,
            ModelMap model) {
        if (productId != null) {
            Optional<Product> optionalProduct = productService.findById(productId);
            if (optionalProduct.isPresent()) {
                discount.setProduct(optionalProduct.get());
            } else {
                model.addAttribute("messageType", "error");
                model.addAttribute("messageContent", "Sản phẩm không tồn tại!");
                return new ModelAndView("redirect:/admin/products", model);
            }
        }

        discountService.save(discount);

        if (discount.getDiscountId() != null) {
            model.addAttribute("messageType", "success");
            model.addAttribute("messageContent", "Mã giảm giá đã được cập nhật thành công!");
        } else {
            model.addAttribute("messageType", "success");
            model.addAttribute("messageContent", "Mã giảm giá đã được lưu thành công!");
        }

        return new ModelAndView("redirect:/admin/products/edit/" + productId, model);
    }

    @GetMapping("delete/{discountId}")
    public ModelAndView deleteDiscount(ModelMap model, @PathVariable("discountId") Integer discountId) {
        Optional<Discount> optionalDiscount = discountService.findById(discountId);

        if (optionalDiscount.isPresent()) {
            discountService.deleteById(discountId);
            model.addAttribute("messageType", "success");
            model.addAttribute("messageContent", "Đã xóa mã giảm giá thành công!");
        } else {
            model.addAttribute("messageType", "error");
            model.addAttribute("messageContent", "Mã giảm giá không tồn tại!");
        }

        return new ModelAndView("redirect:/admin/products/add", model);
    }

}
