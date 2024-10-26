package yourstyle.com.shope.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.service.ColorService;

@Controller
@RequestMapping("/admin/colors")
public class ColorController {

    @Autowired
    private ColorService colorService;

    @PostMapping("/save")
    public String saveColor(@Valid @ModelAttribute("color") Color color, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("colors", colorService.findAll());
            return "admin/products/addOrEdit";
        }
        colorService.save(color);
        return "redirect:/admin/products/add";
    }

    @GetMapping("/delete/{id}")
    public String deleteColor(@PathVariable("id") Integer id, ModelMap model) {
        colorService.deleteById(id);
        model.addAttribute("messageType", "success");
        model.addAttribute("messageContent", "Xóa thành công");
        return "redirect:/admin/products/add"; // Chuyển hướng về trang danh sách
    }
    
}
