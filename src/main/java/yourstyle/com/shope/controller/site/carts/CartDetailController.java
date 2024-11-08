package yourstyle.com.shope.controller.site.carts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.repository.OrderDetailRepository;
import yourstyle.com.shope.repository.ProductVariantRepository;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.OrderService;


@Controller
@RequestMapping("/yourstyle/carts")
public class CartDetailController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;

    @GetMapping("cartdetail")
    public String add(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Integer accountId = userDetails.getAccountId();

        Customer customer = customerRepository.findByAccount_AccountId(accountId);
        Integer customerId = customer != null ? customer.getCustomerId() : null;

        if (customerId != null) {
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_Customer_CustomerId(customerId);
            model.addAttribute("orderDetails", orderDetails);
        } else {
            model.addAttribute("orderDetails", new ArrayList<>());
        }
        return "site/carts/cartdetail";
    }

    @PostMapping("/updateQuantity")
    public String updateQuantity(@RequestParam("orderDetailId") Integer orderDetailId,
                                 @RequestParam("quantity") Integer quantity,
                                 Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/site/accounts/login";
        }
    
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer accountId = userDetails.getAccountId();
    
        // Lấy customerId từ accountId
        Customer customer = customerRepository.findByAccount_AccountId(accountId);
        if (customer == null) {
            return "redirect:/site/accounts/login";
        }
    
        Optional<OrderDetail> orderDetailOptional = orderDetailRepository.findById(orderDetailId);
        if (orderDetailOptional.isPresent()) {
            OrderDetail orderDetail = orderDetailOptional.get();
            
            // Tính sự thay đổi số lượng
            int previousQuantity = orderDetail.getQuantity();
            int quantityDifference = quantity - previousQuantity;
    
            // Cập nhật số lượng trong OrderDetail
            orderDetail.setQuantity(quantity);
            orderDetailRepository.save(orderDetail);
            
            // Cập nhật số lượng trong ProductVariant
            ProductVariant productVariant = orderDetail.getProductVariant();
            
            // Trừ đi số lượng đã được thay đổi trong ProductVariant (nếu có)
            int newProductVariantQuantity = productVariant.getQuantity() - quantityDifference;
            
            // Kiểm tra xem số lượng có hợp lệ không (ví dụ, không thể giảm dưới 0)
            if (newProductVariantQuantity >= 0) {
                productVariant.setQuantity(newProductVariantQuantity);
                productVariantRepository.save(productVariant);
            } else {
                // Nếu không đủ số lượng, có thể thêm thông báo lỗi hoặc khôi phục lại OrderDetail
                // Ví dụ: bạn có thể thông báo cho người dùng là không đủ hàng
                return "redirect:/yourstyle/carts/cartdetail?error=notEnoughStock";
            }
        }
    
        return "redirect:/yourstyle/carts/cartdetail";
    }
    
    @PostMapping("/removeProduct")
    public String removeProductFromCart(@RequestParam("orderDetailId") Integer orderDetailId,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/site/accounts/login";
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer accountId = userDetails.getAccountId();

        // Lấy customerId từ accountId
        Customer customer = customerRepository.findByAccount_AccountId(accountId);
        if (customer == null) {
            return "redirect:/site/accounts/login";
        }

        Optional<OrderDetail> orderDetailOptional = orderDetailRepository.findById(orderDetailId);
        if (orderDetailOptional.isPresent()) {
            OrderDetail orderDetail = orderDetailOptional.get();

            // Tìm biến thể sản phẩm và cập nhật lại số lượng
            ProductVariant productVariant = orderDetail.getProductVariant();
            productVariant.setQuantity(productVariant.getQuantity() + orderDetail.getQuantity());
            productVariantRepository.save(productVariant);

            // Xóa OrderDetail khỏi giỏ hàng
            orderDetailRepository.delete(orderDetail);
        }

        return "redirect:/yourstyle/carts/cartdetail";
    }

}
