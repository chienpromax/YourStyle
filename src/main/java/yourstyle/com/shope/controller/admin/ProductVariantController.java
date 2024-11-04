package yourstyle.com.shope.controller.admin;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Size;
import yourstyle.com.shope.service.CategoryService;
import yourstyle.com.shope.service.ColorService;
import yourstyle.com.shope.service.ProductService;
import yourstyle.com.shope.service.ProductVariantService;
import yourstyle.com.shope.service.SizeService;

@Controller
@RequestMapping("/admin/productVariants")
public class ProductVariantController {

    @Autowired
    private ProductVariantService productVariantService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ColorService colorService;
    @Autowired
    private SizeService sizeService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("add")
    public String addVariant(@RequestParam(value = "categoryId", required = false) Integer categoryId, Model model) {
        List<Category> categories = categoryService.findAll();
        List<Product> products = new ArrayList<>();

        if (categoryId != null) {
            products = productService.findByCategoryId(categoryId);
        }

        List<Color> colors = colorService.findAll();
        List<Size> sizes = sizeService.findAll();

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("colors", colors);
        model.addAttribute("sizes", sizes);
        model.addAttribute("productVariant", new ProductVariant());
        model.addAttribute("categoryId", categoryId);
        return "admin/productVariants/addOrEdit";
    }

    @GetMapping("/search")
    public String searchProductVariants(@RequestParam("value") String value,
                                        @RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size,
                                        Model model) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("product.name")); // Sắp xếp theo tên sản phẩm
    
        Page<ProductVariant> productVariants = productVariantService.searchByProductName(value, pageable);
    
        int totalPages = productVariants.getTotalPages();
        if (totalPages > 0) {
            int start = Math.max(1, currentPage + 1 - 2);
            int end = Math.min(currentPage + 1 + 2, totalPages);
            if (totalPages > 5) {
                if (end == totalPages) {
                    start = end - 5;
                } else if (start == 1) {
                    end = start + 5;
                }
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
    
        model.addAttribute("productVariants", productVariants.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("size", pageSize);
        model.addAttribute("value", value);
    
        return "admin/productVariants/list";
    }

    @GetMapping("edit/{variantId}")
    public ModelAndView edit(ModelMap model, @PathVariable("variantId") Integer variantId) {
        Optional<ProductVariant> optional = productVariantService.findById(variantId);
        List<Color> colors = colorService.findAll();
        List<Size> sizes = sizeService.findAll();
    
        if (optional.isPresent()) {
            ProductVariant productVariant = optional.get();
            model.addAttribute("productVariant", productVariant);
            model.addAttribute("productName", productVariant.getProduct().getName());
            model.addAttribute("categoryName", productVariant.getProduct().getCategory().getName());
            
            model.addAttribute("colors", colors);
            model.addAttribute("sizes", sizes);
            productVariant.setEdit(true);
            return new ModelAndView("admin/productVariants/addOrEdit");
        }
    
        model.addAttribute("messageType", "warning");
        model.addAttribute("messageContent", "Sản phẩm không tồn tại!");
        return new ModelAndView("redirect:/admin/productVariants", model);
    }
    
    @GetMapping("delete/{id}")
    public String deleteProductVariant(@PathVariable("id") Integer id, Model model) {
        if (productVariantService.existsById(id)) {
            productVariantService.deleteById(id);

            model.addAttribute("messageType", "success");
            model.addAttribute("messageContent", "Xóa thành công");
        } else {
            model.addAttribute("messageType", "error");
            model.addAttribute("messageContent", "Sản phẩm không tồn tại");
        }

        return "redirect:/admin/productVariants";
    }

    @PostMapping("saveOrUpdate")
    public ModelAndView saveOrUpdate(ModelMap model, @ModelAttribute("productVariant") ProductVariant productVariant,
                                      BindingResult result) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
    
        // Kiểm tra lỗi đầu vào
        if (result.hasErrors()) {
            model.addAttribute("productVariant", productVariant);
            model.addAttribute("messageType", "error");
            model.addAttribute("messageContent", "Lỗi! Vui lòng kiểm tra lại thông tin.");
            return new ModelAndView("admin/productVariants/addOrEdit", model);
        }
    
        // Kiểm tra sản phẩm đã tồn tại chưa nếu là sửa
        if (productVariant.getProductVariantId() != null && productVariantService.findById(productVariant.getProductVariantId()).isPresent()) {
            productVariantService.update(productVariant);
            model.addAttribute("messageType", "success");
            model.addAttribute("messageContent", "Cập nhật sản phẩm biến thể thành công.");
        } else {
            productVariantService.save(productVariant);
            model.addAttribute("messageType", "success");
            model.addAttribute("messageContent", "Thêm sản phẩm biến thể thành công.");
        }
    
        return new ModelAndView("redirect:/admin/productVariants", model);
    }
    
    @GetMapping("")
    public String list(Model model, @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(0); // Trang hiện tại
        int pageSize = size.orElse(10); // Kích thước trang

        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("productName"));
        Page<ProductVariant> list = productVariantService.findAll(pageable);

        int totalPages = list.getTotalPages();
        if (totalPages > 0) {
            int start = Math.max(1, currentPage + 1 - 2);
            int end = Math.min(currentPage + 1 + 2, totalPages);
            if (totalPages > 10) {
                if (end == totalPages) {
                    start = end - 10;
                } else if (start == 1) {
                    end = start + 10;
                }
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("productVariants", list.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", pageSize);

        return "admin/productVariants/list";
    }

}
