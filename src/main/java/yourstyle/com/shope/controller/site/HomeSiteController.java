package yourstyle.com.shope.controller.site;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.service.CategoryService;

@Controller
@RequestMapping("/yourstyle")
public class HomeSiteController {
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/home")
    public String showHomePage(Model model) {
        List<Category> parentCategories = categoryService.findParentCategories();
        model.addAttribute("parentCategories", parentCategories);
        return "site/pages/home";
    }


}
