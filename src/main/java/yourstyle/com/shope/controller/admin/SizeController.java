package yourstyle.com.shope.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import yourstyle.com.shope.model.Size;
import yourstyle.com.shope.service.SizeService;

@Controller
@RequestMapping("/admin/sizes")
public class SizeController {

    @Autowired
    private SizeService sizeService;

    @PostMapping("/save")
    public String saveSize(@Valid @ModelAttribute("size") Size size, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("sizes", sizeService.findAll());
            return "admin/products/addOrEdit";
        }
        sizeService.save(size);
        return "redirect:/admin/products/add";
    }

    @GetMapping("/delete/{id}")
    public String deleteSize(@PathVariable("id") Integer id, ModelMap model) {
        sizeService.deleteById(id);
        model.addAttribute("messageType", "success");
        model.addAttribute("messageContent", "Xóa thành công");
        return "redirect:/admin/products/add";
    }
}
