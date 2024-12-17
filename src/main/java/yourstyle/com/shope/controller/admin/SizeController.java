package yourstyle.com.shope.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import yourstyle.com.shope.model.Size;
import yourstyle.com.shope.service.SizeService;
import yourstyle.com.shope.service.impl.SizeServiceImpl;

@Controller
@RequestMapping("/admin/sizes")
public class SizeController {

    @Autowired
    private SizeService sizeService;
    @Autowired
    private SizeServiceImpl sizeServiceImpl;

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
    public String deleteSize(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            sizeService.deleteById(id);
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("messageContent", "Xóa thành công");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("messageContent", "Không thể xóa kích thước vì đang được sử dụng!");
        }
        return "redirect:/admin/products/add";
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteColor(@PathVariable("id") Integer id) {
        if (sizeServiceImpl.isSizeInUse(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Không thể xóa! Size đang được sử dụng!");
        }
        sizeServiceImpl.deleteById(id);
        return ResponseEntity.ok("Xóa size thành công!");
    }

}
