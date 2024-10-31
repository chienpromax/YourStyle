package yourstyle.com.shope.controller.site;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.RequestMapping;
import yourstyle.com.shope.model.Customer;
import jakarta.servlet.http.HttpSession;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductFavorite;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.service.ProductFavoriteService;
import yourstyle.com.shope.service.ProductService;

@Controller
public class HomeSiteController {
	@Autowired
	ProductService productService;
	@Autowired
	ProductFavoriteService productFavoriteService;
	@Autowired
	CustomerRepository customerRepository; 

	@RequestMapping({ "/", "/home" })
	public String showHomePage(Model model, HttpSession session) {
		String userName = (String) session.getAttribute("userName");
		model.addAttribute("userName", userName); // Thêm tên người dùng vào mô hình
		List<Product> products = productService.getAllProducts();
		for (Product p : products) {
			System.out.println("Info " + p);
		}		
        Integer accountId = (Integer) session.getAttribute("accountId");
		Customer customer = customerRepository.findByCustomerAccountId(accountId);
        System.out.println("accountId " + accountId);
        if (customer != null) {
			System.out.println("customerId " + customer.getCustomerId());
			List<Product> favoriteProducts = productFavoriteService.getFavoriteProductsByCustomerId(customer.getCustomerId())
			.stream()
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toList());

			System.out.println("product " + favoriteProducts.toString());
            model.addAttribute("favoriteProducts", favoriteProducts);
        }
		model.addAttribute("products", products);

		return "site/pages/home"; // Trả về view home
	}
}
