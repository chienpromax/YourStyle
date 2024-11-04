package yourstyle.com.shope.controller.site;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.service.CategoryService;


@ControllerAdvice
public class NavController {

    @Autowired
    CategoryService categoryService;

    @ModelAttribute("parentCategories")
    public List<Category> getParentCategories() {
        return categoryService.findParentCategories();
    }

 

}
