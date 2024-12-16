package yourstyle.com.shope.controller.site.carts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Discount;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.repository.OrderDetailRepository;
import yourstyle.com.shope.repository.OrderRepository;
import yourstyle.com.shope.repository.ProductVariantRepository;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.ProductService;

@Controller
@RequestMapping("/yourstyle/carts")
public class CartDetailController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("cartdetail")
    public String add(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Integer accountId = userDetails.getAccountId();
        Customer customer = customerRepository.findByAccount_AccountId(accountId);
        Integer customerId = customer != null ? customer.getCustomerId() : null;

        if (customerId != null) {
            List<OrderDetail> orderDetails = orderDetailRepository
                    .findByOrder_Customer_CustomerIdAndOrder_Status(customerId, 9);

            orderDetails.forEach(orderDetail -> {
                Discount discount = orderDetail.getProductVariant().getProduct().getDiscount();
                if (discount != null && !discount.isValid()) {
                    orderDetail.getProductVariant().getProduct().setDiscount(null); // Loại bỏ giảm giá hết hạn
                }
            });

            List<Order> orders = orderService.findByCustomerAndStatus(customer, 9);
            if (!orders.isEmpty()) {
                Order order = orders.get(0); // Lấy Order đang xử lý (status = 9)

                // Tính tổng số tiền có áp dụng mã giảm giá
                BigDecimal totalAmount = calculateTotalAmountWithOrderDiscount(order, orderDetails);
                order.setTotalAmount(totalAmount);
                orderRepository.save(order);

                model.addAttribute("orderDetails", orderDetails);
                model.addAttribute("totalAmount", totalAmount);
            } else {
                model.addAttribute("orderDetails", new ArrayList<>());
                model.addAttribute("totalAmount", BigDecimal.ZERO);
            }
        } else {
            model.addAttribute("orderDetails", new ArrayList<>());
            model.addAttribute("totalAmount", BigDecimal.ZERO);
        }
        List<Product> products = productService.getAllProducts();
        Collections.shuffle(products);
        model.addAttribute("products", products);

        return "site/carts/cartdetail";
    }

    private BigDecimal calculateTotalAmountWithOrderDiscount(Order order, List<OrderDetail> orderDetails) {
        BigDecimal totalAmount = orderDetails.stream()
                .map(orderDetail -> {
                    BigDecimal price = orderDetail.getProductVariant().getProduct().getPrice();
                    int quantity = orderDetail.getQuantity();
    
                    // Kiểm tra giảm giá
                    Discount discount = orderDetail.getProductVariant().getProduct().getDiscount();
                    if (discount != null && discount.isValid()) {
                        BigDecimal discountPercent = discount.getDiscountPercent().divide(BigDecimal.valueOf(100));
                        price = price.subtract(price.multiply(discountPercent));
                    }
    
                    return price.multiply(BigDecimal.valueOf(quantity)); // Tính tiền cho từng sản phẩm
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Cộng tổng
    
        if (order.getVoucher() != null && order.getVoucher().getDiscountAmount() != null) {
            BigDecimal orderDiscountPercent = order.getVoucher().getDiscountAmount().divide(BigDecimal.valueOf(100));
            totalAmount = totalAmount.subtract(totalAmount.multiply(orderDiscountPercent));
        }
    
        return totalAmount.max(BigDecimal.ZERO);
    }
    
    @PostMapping("/updateQuantity")
    public String updateQuantity(@RequestParam("orderDetailId") Integer orderDetailId,
            @RequestParam("quantity") Integer quantity, Model model,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/site/accounts/login";
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer accountId = userDetails.getAccountId();

        Customer customer = customerRepository.findByAccount_AccountId(accountId);
        if (customer == null) {
            return "redirect:/site/accounts/login";
        }

        Optional<OrderDetail> orderDetailOptional = orderDetailRepository.findById(orderDetailId);
        if (orderDetailOptional.isPresent()) {
            OrderDetail orderDetail = orderDetailOptional.get();
            ProductVariant productVariant = orderDetail.getProductVariant();

            int previousQuantity = orderDetail.getQuantity();
            int quantityDifference = quantity - previousQuantity;

            if (quantityDifference > productVariant.getQuantity()) {
                // Thông báo lỗi nếu vượt quá số lượng trong kho
                model.addAttribute("error", "Số lượng bạn chọn vượt quá số lượng trong kho.");
                return "site/carts/cartdetail";
            }

            orderDetail.setQuantity(quantity);
            orderDetailRepository.save(orderDetail);

            int newProductVariantQuantity = productVariant.getQuantity() - quantityDifference;

            if (newProductVariantQuantity >= 0) {
                productVariant.setQuantity(newProductVariantQuantity);
                productVariantRepository.save(productVariant);
            } else {
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

        Customer customer = customerRepository.findByAccount_AccountId(accountId);

        if (customer == null) {
            return "redirect:/site/accounts/login";
        }

        Optional<OrderDetail> orderDetailOptional = orderDetailRepository.findById(orderDetailId);
        if (orderDetailOptional.isPresent()) {
            OrderDetail orderDetail = orderDetailOptional.get();

            ProductVariant productVariant = orderDetail.getProductVariant();
            productVariant.setQuantity(productVariant.getQuantity() + orderDetail.getQuantity());
            productVariantRepository.save(productVariant);

            orderDetailRepository.delete(orderDetail);
        }

        return "redirect:/yourstyle/carts/cartdetail";
    }

}
