package yourstyle.com.shope.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.service.ColorService;
import yourstyle.com.shope.service.impl.ColorServiceImpl;

@Controller
@RequestMapping("/admin/colors")
public class ColorController {

    @Autowired
    private ColorService colorService;
    @Autowired
    ColorServiceImpl colorServiceImpl;

    @PostMapping("/save")
    public String saveColor(@Valid @ModelAttribute("color") Color color, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("colors", colorService.findAll());
            return "admin/products/addOrEdit";
        }
        colorService.save(color);
        return "redirect:/admin/products/add";
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteColor(@PathVariable("id") Integer id) {
        if (colorServiceImpl.isColorInUse(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Không thể xóa! Màu đang được sử dụng!");
        }
        colorService.deleteById(id);
        return ResponseEntity.ok("Xóa màu thành công!");
    }

}
