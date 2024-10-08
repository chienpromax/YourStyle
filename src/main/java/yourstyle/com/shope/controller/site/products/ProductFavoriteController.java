package yourstyle.com.shope.controller.site.products;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProductFavoriteController {
    @RequestMapping({ "/", "/productfavorites" })
    public String showAdminPage() {
        return "site/products/productfavorites";
    }
}
