package yourstyle.com.shope.controller.site.carts;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/yourstyle")
public class SiteOrderController {
    
    @RequestMapping("/order")
    public String showOrder() {
        return "site/carts/order";
    }
    
}
