package yourstyle.com.shope.controller.site.carts;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/yourstyle/order")
public class OrderHistory {
    
    @GetMapping("/orderhistory")
    public String add(Model model) {
        return "site/carts/orderhistory";
    }
}
