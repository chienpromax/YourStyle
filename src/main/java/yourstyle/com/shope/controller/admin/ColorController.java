package yourstyle.com.shope.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.service.ColorService;
import yourstyle.com.shope.validation.admin.ProductDto;

import java.util.List;
import java.util.Optional;
@Controller
@RequestMapping("/admin/colors")
public class ColorController {

    @Autowired
    private ColorService colorService;

    // Phương thức để lưu màu sắc mới hoặc cập nhật màu sắc
    @PostMapping("/save")
    public String saveColor(@Valid @ModelAttribute("color") Color color, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("colors", colorService.findAll());
            return "admin/products/addOrEdit"; // Trở về trang thêm/sửa
        }
        colorService.save(color); // Lưu màu sắc
        return "redirect:/admin/products/add"; // Chuyển hướng về trang danh sách
    }

    // Phương thức để xóa màu sắc
    @GetMapping("/delete/{id}")
    public String deleteColor(@PathVariable("id") Integer id, ModelMap model) {
        colorService.deleteById(id);
        model.addAttribute("messageType", "success");
        model.addAttribute("messageContent", "Xóa thành công");
        return "redirect:/admin/products/add"; // Chuyển hướng về trang danh sách
    }

    // @GetMapping("/edit/{id}")
    // public String editColor(@PathVariable("id") Integer id, Model model) {
    //     Optional<Color> optionalColor = colorService.findById(id);
    //     if (optionalColor.isPresent()) {
    //         model.addAttribute("color", optionalColor.get());
    //     } else {
    //         // Xử lý trường hợp không tìm thấy màu sắc (ví dụ: trả về một trang lỗi hoặc chuyển hướng)
    //         model.addAttribute("messageType", "error");
    //         model.addAttribute("messageContent", "Màu sắc không tồn tại");
    //         return "redirect:/admin/products/add"; // Chuyển hướng về trang danh sách
    //     }
        
    //     model.addAttribute("colors", colorService.findAll()); // Danh sách màu sắc hiện có
    //     return "admin/products/addOrEdit"; // Trả về form để sửa màu sắc
    // }
    
}
