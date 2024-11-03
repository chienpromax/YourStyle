package yourstyle.com.shope.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import io.micrometer.common.util.StringUtils;

import java.util.*;
import java.math.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.springframework.ui.*;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Size;
import yourstyle.com.shope.service.CategoryService;
import yourstyle.com.shope.service.ColorService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderDetailService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.ProductService;
import yourstyle.com.shope.service.ProductVariantService;
import yourstyle.com.shope.service.SizeService;

@Controller
@RequestMapping("admin/sell")
public class SellController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderDetailService orderDetailService;
    @Autowired
    CustomerService customerService;
    @Autowired
    ProductVariantService productVariantService;
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ColorService colorService;
    @Autowired
    SizeService sizeService;

    private void formatProductVariant(List<ProductVariant> productVariants, Model model, DecimalFormat formatter) {
        List<Map<String, String>> productVariantFormatedPrices = new ArrayList<>();
        for (ProductVariant productVariant : productVariants) {
            Map<String, String> priceMap = new HashMap<>();
            // giá cũ
            BigDecimal oldPrice = productVariant.getProduct().getPrice();
            // format giá cũ
            priceMap.put("oldPrice", formatter.format(oldPrice.setScale(0, RoundingMode.FLOOR)) + ".000 VND");
            if (productVariant.getProduct().getDiscount() != null) {
                BigDecimal discountPercent = productVariant.getProduct().getDiscount().getDiscountPercent();
                BigDecimal discountedPrice = oldPrice
                        .multiply(BigDecimal.ONE.subtract(discountPercent.divide(BigDecimal.valueOf(100))));
                priceMap.put("discountedPrice",
                        formatter.format(discountedPrice.setScale(0, RoundingMode.FLOOR)) + ".000 VND");
            }
            productVariantFormatedPrices.add(priceMap);
        }
        model.addAttribute("productVariantFormatedPrices", productVariantFormatedPrices);
    }

    @GetMapping("detail/{orderId}")
    public String sellDetail(@PathVariable("orderId") Integer orderId, Model model) {
        Optional<Order> orderOption = orderService.findById(orderId);
        if (orderOption.isPresent()) {
            Order order = orderOption.get();
            // thông tin địa chỉ giao hàng của khách hàng
            model.addAttribute("order", order);
            // danh sách sản phẩm chi tiết của từng đơn hàng
            if (order.getOrderDetails() != null && order.getOrderId() != null) {
                model.addAttribute("orderDetails", order.getOrderDetails());
            }
            // danh sách sản phẩm chi tiết của từng đơn hàng
            if (order.getOrderDetails() != null && order.getOrderId() != null) {
                model.addAttribute("orderDetails", order.getOrderDetails());
            }
            // xử lý thông tin đơn hàng và format tổng tiền
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
            symbols.setGroupingSeparator('.'); // Dùng dấu '.' cho hàng nghìn
            symbols.setDecimalSeparator(','); // Dùng dấu ',' cho phần thập phân
            DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // định dạng tiền
            // hiển thị danh sách sản phẩm trong modal
            List<ProductVariant> productVariants = productVariantService.findAll();
            model.addAttribute("productVariants", productVariants);
            // format giá sản phẩm
            formatProductVariant(productVariants, model, formatter);
            // hiển thị danh mục trong modal
            List<Category> categories = categoryService.findAll();
            model.addAttribute("categories", categories);
            // hiển thị màu trong modal
            List<Color> colors = colorService.findAll();
            model.addAttribute("colors", colors);
            // hiển thị size trong modal
            List<Size> sizes = sizeService.findAll();
            model.addAttribute("sizes", sizes);

            List<Map<String, String>> orderDetailFormattedPrices = new ArrayList<>();
            // biến tổng tiền dùng để cộng dồn
            BigDecimal totalSum = BigDecimal.ZERO;
            // biến tổng tiền sau khi áp dụng voucher giảm giá
            BigDecimal voucherTotalSum = BigDecimal.ZERO;
            // biến định dạng voucher
            String formattedVoucher = "";
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                Map<String, String> priceMap = new HashMap<>();
                // Giá gốc
                BigDecimal oldPrice = orderDetail.getPrice();
                // đưa vào map để hiển thị giá cũ
                priceMap.put("oldPrice",
                        formatter.format(oldPrice.setScale(0, RoundingMode.FLOOR)) + ".000 VND");
                if (orderDetail.getProductVariant().getProduct().getDiscount() != null) {
                    // lấy phần trăm giảm giá
                    BigDecimal discountPercent = orderDetail.getProductVariant().getProduct().getDiscount()
                            .getDiscountPercent();
                    // lấy giá cũ * phần trăm giảm giá -> giá mới
                    BigDecimal discountedPrice = oldPrice
                            .multiply(BigDecimal.ONE.subtract(discountPercent.divide(BigDecimal.valueOf(100))));
                    // tính cột thành tiền (giá mới đã giảm * số lượng)
                    // Làm tròn xuống bỏ phần thập phân
                    BigDecimal discountIntoMoney = discountedPrice.setScale(0, RoundingMode.FLOOR)
                            .multiply(BigDecimal.valueOf(orderDetail.getQuantity()));
                    // đưa giá sau giảm vào map
                    priceMap.put("discountedPrice",
                            formatter.format(discountedPrice.setScale(0, RoundingMode.FLOOR)) + ".000 VND");
                    // đưa vào map để hiển thị thành tiền
                    priceMap.put("discountIntoMoney",
                            formatter.format(discountIntoMoney) + ".000 VND");
                    // cộng dồn thành tiền đã giảm (hiển thị tổng tiền)
                    totalSum = totalSum.add(discountIntoMoney);
                } else {
                    // Tính thành tiền không giảm giá (số lượng * giá cũ)
                    BigDecimal intoMoney = oldPrice.setScale(0, RoundingMode.FLOOR) // Làm tròn xuống bỏ phần thập phân
                            .multiply(BigDecimal.valueOf(orderDetail.getQuantity()));
                    priceMap.put("intoMoney", formatter.format(intoMoney) + ".000 VND");
                    // cộng dồn thành tiền không giảm (hiển thị tổng tiền)
                    totalSum = totalSum.add(intoMoney);
                }
                // đưa map vào list
                orderDetailFormattedPrices.add(priceMap);
            }
            model.addAttribute("orderDetailFormattedPrices", orderDetailFormattedPrices);
            // tổng tiền
            model.addAttribute("totalSum",
                    formatter.format(totalSum.setScale(0, RoundingMode.FLOOR)) + ".000 VND");
            return "admin/sell/addOrEdit";
        }
        return "admin/sell/list";
    }

    @GetMapping("saveOrUpdate")
    public ModelAndView saveOrUpdate(ModelMap modelMap) {
        // khách hàng lẻ
        Customer customer = customerService.findById(4).get();
        // tạo một đơn hàng
        Order order = new Order();
        order.setTotalAmount(BigDecimal.valueOf(0)); // tổng tiền 0
        order.setStatus(OrderStatus.PLACED);
        order.setNote("");
        order.setTransactionType(null);
        order.setPaymentMethod(null);
        order.setTransactionStatus(null);
        order.setCustomer(customer);
        order.setVoucher(null);
        orderService.save(order);
        // tạo đơn hàng chi tiết
        OrderDetail orderDetail = new OrderDetail();
        ProductVariant productVariant = productVariantService.findById(7).get();
        orderDetail.setOrder(order);
        orderDetail.setProductVariant(productVariant);
        orderDetail.setPrice(BigDecimal.valueOf(0));
        orderDetail.setQuantity(0);
        orderDetailService.save(orderDetail);
        // tìm kiếm danh sách đơn hàng theo khách hàng lẻ
        List<Order> orders = orderService.findByCustomer(customer);
        modelMap.addAttribute("orders", orders);
        return new ModelAndView("admin/sell/list");
    }

    @GetMapping("")
    public String list(Model model) {
        // khách hàng lẻ
        Customer customer = customerService.findById(4).get();
        List<Order> orders = orderService.findByCustomer(customer);
        model.addAttribute("orders", orders);
        return "admin/sell/list";
    }

    @GetMapping("search")
    public String searchProductByInput(Model model, @RequestParam(name = "value", required = false) String value) {
        // xử lý thông tin đơn hàng và format tổng tiền
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.'); // Dùng dấu '.' cho hàng nghìn
        symbols.setDecimalSeparator(','); // Dùng dấu ',' cho phần thập phân
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // định dạng tiền
        List<ProductVariant> productVariants;
        if (org.springframework.util.StringUtils.hasText(value)) {
            if (checkNumber(value)) {
                Integer productVariantId = Integer.valueOf(value);
                productVariants = productVariantService.findByProductVariantId(productVariantId);
            } else {
                productVariants = productVariantService.findByProductNameContaining(value);
            }
        } else {
            productVariants = productVariantService.findAll();
        }
        formatProductVariant(productVariants, model, formatter);
        model.addAttribute("productVariants", productVariants);
        return "admin/sell/fragments/productVariantList :: productRows";
    }

    @GetMapping("searchBySize")
    public String searchProductBySize(Model model, @RequestParam(name = "size", required = false) Integer sizeId) {
        // xử lý thông tin đơn hàng và format tổng tiền
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.'); // Dùng dấu '.' cho hàng nghìn
        symbols.setDecimalSeparator(','); // Dùng dấu ',' cho phần thập phân
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // định dạng tiền
        List<ProductVariant> productVariants;
        Optional<Size> size = sizeService.findById(sizeId);
        if (size.isPresent()) {
            productVariants = productVariantService.findBySize(size.get());
        } else {
            productVariants = productVariantService.findAll();
        }
        formatProductVariant(productVariants, model, formatter);
        model.addAttribute("productVariants", productVariants);
        return "admin/sell/fragments/productVariantList :: productRows";
    }

    @GetMapping("searchByColor")
    public String searchProductByColor(Model model, @RequestParam(name = "color", required = false) Integer colorId) {
        // xử lý thông tin đơn hàng và format tổng tiền
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.'); // Dùng dấu '.' cho hàng nghìn
        symbols.setDecimalSeparator(','); // Dùng dấu ',' cho phần thập phân
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // định dạng tiền
        List<ProductVariant> productVariants;
        Optional<Color> color = colorService.findById(colorId);
        if (color.isPresent()) {
            productVariants = productVariantService.findByColor(color.get());
        } else {
            productVariants = productVariantService.findAll();
        }
        formatProductVariant(productVariants, model, formatter);
        model.addAttribute("productVariants", productVariants);
        return "admin/sell/fragments/productVariantList :: productRows";
    }

    @GetMapping("searchByCategory")
    public String searchProductByCategory(Model model,
            @RequestParam(name = "category", required = false) Integer categoryId) {
        // xử lý thông tin đơn hàng và format tổng tiền
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.'); // Dùng dấu '.' cho hàng nghìn
        symbols.setDecimalSeparator(','); // Dùng dấu ',' cho phần thập phân
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // định dạng tiền
        // khởi tạo danh sách sản phẩm biến thể
        List<ProductVariant> productVariants = new ArrayList<>();
        Optional<Category> Category = categoryService.findById(categoryId);
        if (Category.isPresent()) {
            List<Product> products = productService.findByCategory(Category.get());
            for (Product product : products) {
                List<ProductVariant> variants = productVariantService.findByProduct(product);
                productVariants.addAll(variants);
            }
        } else {
            productVariants = productVariantService.findAll();
        }
        formatProductVariant(productVariants, model, formatter);
        model.addAttribute("productVariants", productVariants);
        return "admin/sell/fragments/productVariantList :: productRows";
    }

    @SuppressWarnings("unused")
    private boolean checkNumber(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?"); // kiểm tra chuỗi có phải là số hay không
    }
}
