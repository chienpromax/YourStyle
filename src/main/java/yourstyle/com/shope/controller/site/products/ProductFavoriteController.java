package yourstyle.com.shope.controller.site.products;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.ProductFavoriteService;

@Controller
@RequestMapping("/yourstyle/productfavorites")
public class ProductFavoriteController {

	@Autowired
	private ProductFavoriteService productFavoriteService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private CustomerRepository customerRepository;

	@RequestMapping("")
	public String showFavoriteProducts(HttpSession session, Model model) {
		Integer accountId = (Integer) session.getAttribute("accountId");

		if (accountId == null) {
			return "site/accounts/login";
		}

		Customer customer = customerRepository.findByCustomerAccountId(accountId);
		System.out.println("accountId: " + accountId);

		if (customer != null) {
			System.out.println("customerId: " + customer.getCustomerId());

			List<Product> favoriteProducts = productFavoriteService
					.getFavoriteProductsByCustomerId(customer.getCustomerId()).stream().filter(Optional::isPresent)
					.map(Optional::get).collect(Collectors.toList());

			System.out.println("favoriteProducts: " + favoriteProducts.toString());
			model.addAttribute("favoriteProducts", favoriteProducts);
		} else {
			return "site/accounts/login";
		}

		return "site/products/productfavorites";
	}

	@GetMapping("/add")
	public String addFavorite(@RequestParam("productId") int productId, HttpSession session , RedirectAttributes redirectAttributes) {
		Integer accountId = (Integer) session.getAttribute("accountId");

		if (accountId == null) {
			// cái chỗ ni mình gắn attribute mess vào
//			return ResponseEntity.status(401).body("Yêu cầu đăng nhập.");
		}
		System.out.println("Thêm sản phẩm yêu thích với customerId: " + accountId + " và productId: " + productId);
		System.out.println("Id của sản phẩm: " + productId);

		if (accountId != null) {
			Customer customer = customerRepository.findByCustomerAccountId(accountId);
			boolean added = productFavoriteService.addProductToFavorite(customer.getCustomerId(), productId);
			if (added) {
				redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được thêm vào danh sách yêu thích!");
			}
		}
		return "redirect:/yourstyle/productfavorites";
	}

	@GetMapping("/removefavorite/{id}")
	public String removeFavorite(@PathVariable("id") int productId, HttpSession session,
			RedirectAttributes redirectAttributes) {
		Integer accountId = (Integer) session.getAttribute("accountId");

		if (accountId == null) {
			return "site/accounts/login";
		}

		Customer customer = customerRepository.findByCustomerAccountId(accountId);

		if (customer != null) {
			boolean removed = productFavoriteService.removeProductFromFavorite(customer.getCustomerId(), productId);

			if (removed) {
				redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được xóa khỏi danh sách yêu thích!");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage",
						"Sản phẩm không tồn tại trong danh sách yêu thích.");
			}
		} else {
			return "site/accounts/login";
		}

		return "redirect:/yourstyle/productfavorites";
	}

}
