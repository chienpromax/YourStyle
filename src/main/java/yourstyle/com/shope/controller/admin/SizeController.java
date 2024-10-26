package yourstyle.com.shope.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import yourstyle.com.shope.model.Size;

@Controller
public class SizeController {

    @GetMapping("/admin/sizes/saveOrUpdate")
    public String showSizeForm(Model model) {
        model.addAttribute("size", new Size());
        return "admin/products/addOrEdit";
    }

    @PostMapping("/admin/sizes/saveOrUpdate")
    public String saveOrUpdateSize(@ModelAttribute("size") Size size) {

        return "redirect:/admin/sizes";
    }
}
