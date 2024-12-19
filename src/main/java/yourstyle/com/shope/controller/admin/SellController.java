package yourstyle.com.shope.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.math.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import org.springframework.ui.*;
import org.springframework.util.StringUtils;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderChannel;
import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Size;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.AddressService;
import yourstyle.com.shope.service.CategoryService;
import yourstyle.com.shope.service.ColorService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderDetailService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.ProductService;
import yourstyle.com.shope.service.ProductVariantService;
import yourstyle.com.shope.service.SizeService;
import yourstyle.com.shope.validation.admin.AddressDto;

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
    @Autowired
    AddressService addressService;
    @Autowired
    AccountService accountService;

    List<Integer> statuses = Arrays.asList(0, 2, 3, 4, 5, 6, 8);

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
        Page<Customer> list = customerService.findAllNotRetailCustomer(4, pageable);

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
            @RequestParam("size") Optional<Integer> size) {
        Optional<Order> orderOption = orderService.findById(orderId);
        if (orderOption.isPresent()) {
            Order order = orderOption.get();
            // thông tin địa chỉ giao hàng của khách hàng
            model.addAttribute("order", order);
            // thêm địa chỉ cho khách hàng bỏ vào object
            model.addAttribute("address", new AddressDto());
            if (order.getCustomer() != null && order.getCustomer().getCustomerId() != null) {
                model.addAttribute("customer", order.getCustomer());
            }
            // danh sách sản phẩm chi tiết của từng đơn hàng
            if (order.getOrderDetails() != null && order.getOrderId() != null) {
                model.addAttribute("orderDetails", order.getOrderDetails());
            }
            // danh sách khách hàng
            int currentPage = page.orElse(0);
            int pageSize = size.orElse(5);
            populateCustomerList(model, currentPage, pageSize);
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
                // lấy kiểu giảm giá
                int voucherType = order.getVoucher().getType();
                BigDecimal voucherValue = order.getVoucher().getDiscountAmount().setScale(0, RoundingMode.FLOOR);
                BigDecimal minTotalVoucher = order.getVoucher().getMinTotalAmount().setScale(0, RoundingMode.FLOOR);
                switch (voucherType) {
                    case 1: // giảm giá tiền trực tiếp
                        // tính tổng tiền sau khi giảm voucher
                        voucherTotalSum = totalSum
                                .subtract(voucherValue);
                        // định dạng voucher giảm giá 50.000 VND
                        formattedVoucher = formatter.format(voucherValue) + ".000 VND";
                        break;
                    case 2: // giảm giá tiền theo phần trăm
                        voucherTotalSum = totalSum.multiply(BigDecimal.ONE
                                .subtract(voucherValue.divide(BigDecimal.valueOf(100))));
                        // định dạng voucher giảm giá %
                        formattedVoucher = formatter.format(voucherValue) + "%";
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
            model.addAttribute("formattedVoucher", formattedVoucher);
            return "admin/sell/addOrEdit";
        }
        return "admin/sell/list";
    }

    // xử lý tính tổng số lượng của đơn hàng
    Map<Integer, Integer> totalQuantities = new HashMap<>();
    Map<Integer, String> totalAmounts = new HashMap<>();

    public void totalQuantitiesAndTotalAmounts(Page<Order> list, Model model) {
        // xử lý thông tin đơn hàng và format tổng tiền
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.'); // Dùng dấu '.' cho hàng nghìn
        symbols.setDecimalSeparator(','); // Dùng dấu ',' cho phần thập phân
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // định dạng tiền
        for (Order order : list) {
            // lấy đơn hàng vừa tìm được lấy danh sách đơn hàng chi tiết
            List<OrderDetail> orderDetails = order.getOrderDetails();
            // tính tổng số lượng trong đơn hàng chi tiết của mỗi đơn hàng
            int totalQuantity = orderDetails.stream().mapToInt((OrderDetail::getQuantity)).sum();
            // đưa tổng số lượng vào map
            totalQuantities.put(order.getOrderId(), totalQuantity);
            totalAmounts.put(order.getOrderId(),
                    formatter.format(order.getTotalAmount().setScale(0, RoundingMode.FLOOR)) + ".000 VND");
            // chia sẻ tổng số lượng và tổng tiền qua model để hiển thị trong view
            model.addAttribute("totalQuantities", totalQuantities);
            model.addAttribute("totalAmounts", totalAmounts);
        }
    }

    @GetMapping("")
    public String list(Model model, @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(0); // trang hiện tại
        int pageSize = size.orElse(10); // mặc định hiển thị 10 hóa đơn 1 trang
        // khách hàng lẻ
        // Customer customer = customerService.findById(4).get();
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "orderDate"));
        Page<Order> orders = orderService.findByOrderChannelNotStatusComplete(OrderChannel.IN_STORE, statuses,
                pageable);
        paginationOrders(orders, currentPage, model);
        totalQuantitiesAndTotalAmounts(orders, model);
        model.addAttribute("orders", orders);
        return "admin/sell/list";
    }

    public void paginationOrders(Page<Order> list, int currentPage, Model model) {
        int totalPages = list.getTotalPages(); // lấy tổng số trang
        if (totalPages > 0) {
            // 1 2 3 4 5
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
    }

    @GetMapping("searchListOrderInStore")
    public String searchAccount(Model model, @RequestParam(name = "value", required = false) String value,
            @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(0); // trang hiện tại
        int pageSize = size.orElse(10); // mặc định hiển thị 10 hóa đơn 1 trang
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "orderDate"));
        Page<Order> list = null;
        if (StringUtils.hasText(value)) { // kiểm tra có giá trị hay không
            if (checkNumber(value)) {
                Integer orderId = Integer.valueOf(value);
                List<OrderChannel> orderChannels = Arrays.asList(OrderChannel.ONLINE, OrderChannel.DIRECT);
                list = orderService.findByOrderId(orderId, orderChannels, pageable);
            } else {
                list = orderService.findByOrderChannelNotStatusComplete(OrderChannel.IN_STORE, statuses, pageable);
            }
        } else {
            list = orderService.findByOrderChannelNotStatusComplete(OrderChannel.IN_STORE, statuses, pageable);
        }
        totalQuantitiesAndTotalAmounts(list, model);
        model.addAttribute("orders", list);
        return "admin/sell/fragments/orderInStoreList ::orderInStoreRows";
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

    @GetMapping("searchByCustomer")
    public String searchCustomer(org.springframework.ui.Model model,
            @RequestParam("value") Optional<String> value,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(5);
        // tìm kiếm khách hàng
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("fullname"));
        Page<Customer> customers;
        String searchValue = value.orElse("");
        if (checkPhoneNumber(searchValue)) {
            System.out.println("kiểm tra số: " + checkNumber(searchValue));
            customers = customerService.findByPhoneName(4, searchValue,
                    pageable);
        } else {
            customers = customerService.findByFullnameContaining(4, searchValue,
                    pageable);
        }
        model.addAttribute("Customers", customers.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("size", pageSize);
        model.addAttribute("value", searchValue);
        return "admin/sell/fragments/CustomerList :: customerRows";
    }

    @RequestMapping(value = "/addCustomer", method = RequestMethod.POST)
    public String addCustomer(@RequestBody AddressDto addressDto, Model model) {
        // tài khoản nhân viên vai trò khách
        Account account = accountService.findById(2).get();
        // tạo khách hàng
        Customer customer = new Customer();
        customer.setFullname(addressDto.getFullname());
        customer.setPhoneNumber(addressDto.getPhoneNumber());
        customer.setGender(addressDto.isGender());
        customer.setAccount(account);
        customerService.save(customer);
        // tạo địa chỉ
        Address address = new Address();
        address.setStreet(addressDto.getStreet());
        address.setWard(addressDto.getWard());
        address.setDistrict(addressDto.getDistrict());
        address.setCity(addressDto.getCity());
        address.setIsDefault(true); // tạo khách hàng và cho địa chỉ mặc định
        address.setCustomer(customer);
        addressService.save(address);
        int currentPage = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("fullname"));
        Page<Customer> customers = customerService.findByFullnameContaining(4, "",
                pageable); // tìm kiếm tất cả khách hàng nhưng không có khách lẻ
        model.addAttribute("Customers", customers.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("size", pageSize);
        return "admin/sell/fragments/CustomerList :: customerRows";
    }

    private boolean checkNumber(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?"); // kiểm tra chuỗi có phải là số hay không
    }

    private boolean checkPhoneNumber(String str) {
        return str != null && str.matches("\\d+"); // kiểm tra chuỗi chỉ chứa các chữ số
    }

}
