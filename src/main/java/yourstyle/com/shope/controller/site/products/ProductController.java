package yourstyle.com.shope.controller.site.products;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Size;
import yourstyle.com.shope.service.ColorService;
import yourstyle.com.shope.service.ProductService;
import yourstyle.com.shope.service.ProductVariantService;
import yourstyle.com.shope.service.SizeService;

@Controller
@RequestMapping("/allproduct")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    ColorService colorService;
    @Autowired
    SizeService sizeService;
    @Autowired
    ProductVariantService productVariantService;

    @GetMapping
    public String getAllProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort,
            Model model) {
        List<Color> colors = colorService.findAll();
        List<Size> sizes = sizeService.findAll();
        Pageable pageable = PageRequest.of(page, size);
        List<Product> products;

        if ("best-sellers".equals(sort)) {
            // Lấy danh sách sản phẩm bán chạy nhất
            products = productService.getBestSellingProducts();
        } else if ("discount".equals(sort)) {
            // Lấy danh sách sản phẩm có giảm giá
            products = productService.getDiscountedProducts();
        } else if (categoryId != null) {
            // Lọc theo danh mục nếu categoryId không null
            Page<Product> productPage = productService.findByCategory_CategoryId(categoryId, pageable);
            products = productPage.getContent();
        } else {
            // Lấy tất cả sản phẩm nếu không có điều kiện sắp xếp
            Page<Product> productPage = productService.findAll(pageable);
            products = productPage.getContent();
        }
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (products.size() + size - 1) / size);
        model.addAttribute("sort", sort);
        model.addAttribute("colors", colors);
        model.addAttribute("sizes", sizes);
        return "site/products/allproduct";
    }

    // lọc theo giá thấp đến giá cao
    @GetMapping("filterMinPriceAndMaxPrice")
    public String filterMinPriceAndMaxPrice(Model model, @RequestParam("minPrice") Optional<Long> minPrice,
            @RequestParam("maxPrice") Optional<Long> maxPrice,
            @RequestParam("categoryId") Integer categoryId) {
        if (minPrice.isPresent() && maxPrice.isPresent()) {
            BigDecimal minPriceDecimal = BigDecimal.valueOf(minPrice.get());
            BigDecimal maxPriceDecimal = BigDecimal.valueOf(maxPrice.get());
            List<Product> products = productService.findByCategoryId(categoryId);
            List<Product> productsReponse = new ArrayList<>();
            for (Product product : products) {
                if (product.getDiscount() != null) {
                    BigDecimal discount = BigDecimal.ONE
                            .subtract(product.getDiscount().getDiscountPercent().divide(BigDecimal.valueOf(100)));
                    BigDecimal priceDiscounted = product.getPrice().multiply(discount);
                    // Lọc sản phẩm có giá đã giảm nằm trong khoảng giá yêu cầu
                    if (priceDiscounted.compareTo(minPriceDecimal) >= 0
                            && priceDiscounted.compareTo(maxPriceDecimal) <= 0) {
                        productsReponse.add(product);
                    }
                } else {
                    BigDecimal price = product.getPrice();
                    if (price.compareTo(minPriceDecimal) >= 0 && price.compareTo(maxPriceDecimal) <= 0) {
                        productsReponse.add(product);
                    }
                }
            }
            model.addAttribute("products", productsReponse);
            return "/site/products/fragments/productlist :: productRows";
        }
        return "/site/products/fragments/productlist :: productRows";
    }

    // lọc theo màu và kích thước
    @GetMapping("filterProduct")
    public String filterSize(Model model, @RequestParam(name = "sizeId", required = false) Integer sizeId,
            @RequestParam(name = "colorId", required = false) Integer colorId,
            @RequestParam("categoryId") Integer categoryId) {
        if (sizeId != null) {
            Optional<Size> sizeOptional = sizeService.findById(sizeId);
            Size size = sizeOptional.get();
            if (sizeOptional.isPresent()) {
                List<ProductVariant> productVariants = productVariantService.findBySize(size, categoryId);
                List<Product> products = productVariants.stream()
                        .map((ProductVariant::getProduct))
                        .distinct() // không có sản phẩm trùng lập
                        .toList();

                model.addAttribute("products", products);
            }
        }
        if (colorId != null) {
            Optional<Color> colorOptional = colorService.findById(colorId);
            Color color = colorOptional.get();
            if (colorOptional.isPresent()) {
                List<ProductVariant> productVariants = productVariantService.findByColor(color, categoryId);
                List<Product> products = productVariants.stream()
                        .map((ProductVariant::getProduct))
                        .distinct() // không có sản phẩm trùng lập
                        .toList();

                model.addAttribute("products", products);
            }
        }
        if (colorId != null && sizeId != null) {
            Optional<Color> colorOptional = colorService.findById(colorId);
            Optional<Size> sizeOptional = sizeService.findById(sizeId);
            Size size = sizeOptional.get();
            Color color = colorOptional.get();
            if (sizeOptional.isPresent() && colorOptional.isPresent()) {
                List<ProductVariant> productVariants = productVariantService.findBySizeAndColor(size, color,
                        categoryId);
                List<Product> products = productVariants.stream()
                        .map((ProductVariant::getProduct))
                        .distinct() // không có sản phẩm trùng lập
                        .toList();

                model.addAttribute("products", products);
            }
        }

        return "/site/products/fragments/productlist :: productRows";
    }

    // lọc combobox
    @GetMapping("sortProduct")
    public String sortProduct(Model model, @RequestParam("typeSort") Integer typeSort,
            @RequestParam("categoryId") Integer categoryId) {
        List<Product> products = productService.findByCategoryId(categoryId);
        switch (typeSort) {
            case 1: // sắp xếp giảm dần
                products = products.stream()
                        .sorted((p1, p2) -> {
                            // giá sản phẩm thứ 1
                            BigDecimal priceOld1 = p1.getPrice() != null ? p1.getPrice() : BigDecimal.ZERO;
                            BigDecimal discountedPrice1 = p1.getDiscount() != null
                                    ? p1.getDiscountedPrice()
                                    : priceOld1;
                            // giá sản phẩm thứ 2
                            BigDecimal priceOld2 = p2.getPrice() != null ? p2.getPrice() : BigDecimal.ZERO;
                            BigDecimal discountedPrice2 = p2.getDiscount() != null
                                    ? p2.getDiscountedPrice()
                                    : priceOld2;
                            return discountedPrice2.compareTo(discountedPrice1);
                        })
                        .collect(Collectors.toList());
                break;
            case 2:
                products = products.stream()
                        .sorted((p1, p2) -> {
                            // giá sản phẩm thứ 1
                            BigDecimal priceOld1 = p1.getPrice() != null ? p1.getPrice() : BigDecimal.ZERO;
                            BigDecimal discountedPrice1 = p1.getDiscount() != null
                                    ? p1.getDiscountedPrice()
                                    : priceOld1;
                            // giá sản phẩm thứ 2
                            BigDecimal priceOld2 = p2.getPrice() != null ? p2.getPrice() : BigDecimal.ZERO;
                            BigDecimal discountedPrice2 = p2.getDiscount() != null
                                    ? p2.getDiscountedPrice()
                                    : priceOld2;
                            return discountedPrice1.compareTo(discountedPrice2);
                        })
                        .collect(Collectors.toList());
                break;
            case 3:
                products = products.stream()
                        .sorted((p1, p2) -> {
                            ProductVariant latestProductVariant1 = p1.getProductVariants().stream()
                                    .max(Comparator.comparing(ProductVariant::getCreateAt))
                                    .orElseThrow(() -> new RuntimeException("ProductVariant not found"));
                            ProductVariant latestProductVariant2 = p2.getProductVariants().stream()
                                    .max(Comparator.comparing(ProductVariant::getCreateAt))
                                    .orElseThrow(() -> new RuntimeException("ProductVariant not found"));
                            return latestProductVariant2.getCreateAt().compareTo(latestProductVariant1.getCreateAt());
                        })
                        .collect(Collectors.toList());
                break;
            default:
                break;
        }
        model.addAttribute("products", products);
        return "/site/products/fragments/productlist :: productRows";
    }

    // lọc giá theo range price
    @GetMapping("filterRangePrice")
    public String filterRangePrice(Model model, @RequestParam(name = "price", required = false) BigDecimal price,
            @RequestParam("categoryId") Integer categoryId) {
        List<Product> products;
        if (price != null) {
            products = productService.findByPriceLessThanEqualAndCategoryId(price, categoryId);
        } else {
            products = productService.findByCategoryId(categoryId);
        }
        model.addAttribute("products", products);
        return "/site/products/fragments/productlist :: productRows";
    }
}
