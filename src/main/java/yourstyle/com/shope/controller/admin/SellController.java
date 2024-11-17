package yourstyle.com.shope.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.math.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import org.springframework.ui.*;
import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderDetail;
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

    private void formatProductVariant(List<ProductVariant> productVariants, Model model) {
        // xử lý thông tin đơn hàng và format tổng tiền
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.'); // Dùng dấu '.' cho hàng nghìn
        symbols.setDecimalSeparator(','); // Dùng dấu ',' cho phần thập phân
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // định dạng tiền
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

    private void populateCustomerList(Model model, int currentPage, int pageSize) {
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("fullname"));
        Page<Customer> list = customerService.findAll(pageable);

        int totalPages = list.getTotalPages();
        if (totalPages > 0) {
            int start = Math.max(1, currentPage + 1 - 2);
            int end = Math.min(currentPage + 1 + 2, totalPages);
            if (totalPages > 5) {
                if (end == totalPages) {
                    start = end - 5;
                } else if (start == 1) {
                    end = start + 5;
                }
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("Customers", list.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", pageSize);
    }

    @GetMapping("detail/{orderId}")
    public String sellDetail(@PathVariable("orderId") Integer orderId, Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("value") Optional<String> value) {
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
            // danh sách địa chỉ khách hàng
            int currentPage = page.orElse(0);
            int pageSize = size.orElse(5);
            populateCustomerList(model, currentPage, pageSize);
            // tìm kiếm khách hàng
            String searchValue = value.orElse("");
            Page<Customer> customers = customerService.searchByNameOrPhone(searchValue,
                    PageRequest.of(currentPage, pageSize, Sort.by("fullname")));
            model.addAttribute("Customers", customers.getContent());
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("size", pageSize);
            model.addAttribute("value", searchValue);
            // xử lý thông tin đơn hàng và format tổng tiền
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
            symbols.setGroupingSeparator('.'); // Dùng dấu '.' cho hàng nghìn
            symbols.setDecimalSeparator(','); // Dùng dấu ',' cho phần thập phân
            DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // định dạng tiền
            // hiển thị danh sách sản phẩm trong modal
            List<ProductVariant> productVariants = productVariantService.findAll();
            model.addAttribute("productVariants", productVariants);
            // format giá sản phẩm
            formatProductVariant(productVariants, model);
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
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                Map<String, String> priceMap = new HashMap<>();
                // Giá gốc
                BigDecimal oldPrice = orderDetail.getProductVariant().getProduct().getPrice();
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
                    formatter.format(totalSum.setScale(0, RoundingMode.DOWN)) + ".000 VND");

            // biến tổng tiền sau khi áp dụng voucher giảm giá
            BigDecimal voucherTotalSum = BigDecimal.ZERO;
            // biến định dạng voucher
            String formattedVoucher = "";
            // tính phiếu vouchernếu có
            if (order.getVoucher() != null) {
                // định dạng voucher giảm giá 50.000 VND
                BigDecimal discountVoucher = order.getVoucher().getDiscountAmount().setScale(0,
                        RoundingMode.FLOOR);
                // lấy kiểu giảm giá
                int voucherType = order.getVoucher().getType();
                BigDecimal voucherValue = order.getVoucher().getDiscountAmount().setScale(0, RoundingMode.FLOOR);
                BigDecimal minTotalVoucher = order.getVoucher().getMinTotalAmount().setScale(0, RoundingMode.FLOOR);
                switch (voucherType) {
                    case 1: // giảm giá tiền trực tiếp
                        // tính tổng tiền sau khi giảm voucher
                        voucherTotalSum = totalSum
                                .subtract(voucherValue)
                                .add(BigDecimal.valueOf(32));
                        // định dạng voucher giảm giá 50.000 VND
                        formattedVoucher = formatter.format(discountVoucher) + ".000 VND";
                        break;
                    case 2: // giảm giá tiền theo phần trăm
                        voucherTotalSum = totalSum.multiply(BigDecimal.ONE
                                .subtract(voucherValue.divide(BigDecimal.valueOf(100))))
                                .add(BigDecimal.valueOf(32));
                        // định dạng voucher giảm giá %
                        formattedVoucher = formatter.format(discountVoucher) + "%";
                        break;
                    case 3: // Miển phí vận chuyển nếu đủ điều kiện
                        // thành tiền đơn hàng lớn hơn hoặc bằng 300.000đ thì miển phí vận chuyển cho
                        // đơn hàng đó
                        if (totalSum.compareTo(minTotalVoucher) >= 0) { // trả về 1 và 0
                            voucherTotalSum = totalSum.subtract(BigDecimal.valueOf(32));
                            model.addAttribute("shippingFee", formatter.format(0) + " VND");
                        } else { // trả về - 1 nếu thấp hơn tổng tiền tối thiểu
                            voucherTotalSum = totalSum.add(BigDecimal.valueOf(32));
                            model.addAttribute("shippingFee", formatter.format(32) + ".000 VND");
                        }
                        break;
                }

            } else { // ngược lại không có giảm giá
                voucherTotalSum = totalSum;
                formattedVoucher = "0 VND";
            }
            // Tổng tiền sau khi giảm voucher
            model.addAttribute("voucherTotalSum", formatter.format(voucherTotalSum) + ".000 VND");
            return "admin/sell/addOrEdit";
        }
        return "admin/sell/list";
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
        formatProductVariant(productVariants, model);
        model.addAttribute("productVariants", productVariants);
        return "admin/sell/fragments/productVariantList :: productRows";
    }

    @GetMapping("searchBySize")
    public String searchProductBySize(Model model, @RequestParam(name = "size", required = false) Integer sizeId) {
        List<ProductVariant> productVariants;
        Optional<Size> size = sizeService.findById(sizeId);
        if (size.isPresent()) {
            productVariants = productVariantService.findBySize(size.get());
        } else {
            productVariants = productVariantService.findAll();
        }
        formatProductVariant(productVariants, model);
        model.addAttribute("productVariants", productVariants);
        return "admin/sell/fragments/productVariantList :: productRows";
    }

    @GetMapping("searchByColor")
    public String searchProductByColor(Model model, @RequestParam(name = "color", required = false) Integer colorId) {
        List<ProductVariant> productVariants;
        Optional<Color> color = colorService.findById(colorId);
        if (color.isPresent()) {
            productVariants = productVariantService.findByColor(color.get());
        } else {
            productVariants = productVariantService.findAll();
        }
        formatProductVariant(productVariants, model);
        model.addAttribute("productVariants", productVariants);
        return "admin/sell/fragments/productVariantList :: productRows";
    }

    @GetMapping("searchByCategory")
    public String searchProductByCategory(Model model,
            @RequestParam(name = "category", required = false) Integer categoryId) {
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
        formatProductVariant(productVariants, model);
        model.addAttribute("productVariants", productVariants);
        return "admin/sell/fragments/productVariantList :: productRows";
    }

    private boolean checkNumber(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?"); // kiểm tra chuỗi có phải là số hay không
    }
}
