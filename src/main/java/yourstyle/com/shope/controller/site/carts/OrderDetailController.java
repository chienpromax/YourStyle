package yourstyle.com.shope.controller.site.carts;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/yourstyle")
public class OrderDetailController {

    @RequestMapping("/orderdetail")
    public String showCartDetail() {
        return "site/carts/orderdetail";
    }
}
