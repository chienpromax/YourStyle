package yourstyle.com.shope.controller.site.products;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

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
    public String showFavoriteProducts(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/yourstyle/accounts/login"; // Redirect to login if not authenticated
        }

        String username = authentication.getName();
        Integer accountId = accountService.findByUsername(username).getAccountId(); // Replace with your method to get accountId

        Customer customer = customerRepository.findByCustomerAccountIdWithQuery(accountId);
        if (customer != null) {
            List<Product> favoriteProducts = productFavoriteService
                    .getFavoriteProductsByCustomerId(customer.getCustomerId())
                    .stream().filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            model.addAttribute("favoriteProducts", favoriteProducts);
        } else {
            return "redirect:/yourstyle/accounts/login"; // Redirect if customer not found
        }

        return "site/products/productfavorites";
    }

    @GetMapping("/add")
    public String addFavorite(@RequestParam("productId") int productId, Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập trước khi thêm sản phẩm yêu thích.");
            return "redirect:/yourstyle/accounts/login"; // Redirect to login if not authenticated
        }

        String username = authentication.getName();
        Integer accountId = accountService.findByUsername(username).getAccountId(); // Replace with your method to get accountId
        Customer customer = customerRepository.findByCustomerAccountIdWithQuery(accountId);

        if (customer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin khách hàng.");
            return "redirect:/yourstyle/productfavorites";
        }

        boolean added = productFavoriteService.addProductToFavorite(customer.getCustomerId(), productId);

        if (added) {
        	System.out.println("okke notification ...");
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được thêm vào danh sách yêu thích!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể thêm sản phẩm vào danh sách yêu thích.");
        }

        return "redirect:/yourstyle/productfavorites";
    }

    @GetMapping("/removefavorite/{id}")
    public String removeFavorite(@PathVariable("id") int productId, Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/yourstyle/accounts/login"; // Redirect to login if not authenticated
        }

        String username = authentication.getName();
        Integer accountId = accountService.findByUsername(username).getAccountId(); // Replace with your method to get accountId
        Customer customer = customerRepository.findByCustomerAccountIdWithQuery(accountId);

        if (customer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin khách hàng.");
            return "redirect:/yourstyle/productfavorites";
        }

        boolean removed = productFavoriteService.removeProductFromFavorite(customer.getCustomerId(), productId);

        if (removed) {
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được xóa khỏi danh sách yêu thích!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm không tồn tại trong danh sách yêu thích.");
        }

        return "redirect:/yourstyle/productfavorites";
    }
}
