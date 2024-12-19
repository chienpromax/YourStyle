package yourstyle.com.shope.controller.site.products;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.stream.IntStream;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductVariant;
import yourstyle.com.shope.model.Size;
import yourstyle.com.shope.service.ColorService;
import yourstyle.com.shope.service.ProductService;
import yourstyle.com.shope.service.ProductVariantService;
import yourstyle.com.shope.service.SizeService;

@Controller
@RequestMapping("/yourstyle/allproduct")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    ColorService colorService;
    @Autowired
    SizeService sizeService;
    @Autowired
    ProductVariantService productVariantService;

    public void paginationNumber(Page<Product> productPage, int page, Model model) {
        int totalPages = productPage.getTotalPages();
        if (totalPages > 0) {
            int start = Math.max(1, page + 1 - 2);
            int end = Math.min(page + 1 + 2, totalPages);
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

    public void dataPagination(Model model, int page, int size, List<Product> products, Integer categoryId) {
        // danh sách phân trang
        int start = page * size;
        int end = Math.min(start + size, products.size());
        List<Product> paginationProducts = products.subList(start, end);

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPages = new PageImpl<>(paginationProducts, pageable, products.size());
        paginationNumber(productPages, page, model);
        model.addAttribute("products", paginationProducts);
        model.addAttribute("productPages", productPages);
        model.addAttribute("categoryId", categoryId);
    }

    @GetMapping
    public String getAllProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String sort,
            Model model) {
        List<Color> colors = colorService.findAll();
        List<Size> sizes = sizeService.findAll();
        Pageable pageable = PageRequest.of(page, size);
        List<Product> products;
        Page<Product> productPage = null;
        if ("best-sellers".equals(sort)) {
            // Lấy danh sách sản phẩm bán chạy nhất
            products = productService.getBestSellingProducts();
            productPage = new PageImpl<>(products, pageable, products.size());

        } else if ("discount".equals(sort)) {
            // Lấy danh sách sản phẩm có giảm giá
            products = productService.getDiscountedProducts();
            productPage = new PageImpl<>(products, pageable, products.size());

        } else if (categoryId != null) {
            // Lọc theo danh mục nếu categoryId không null
            productPage = productService.findByCategory_CategoryId(categoryId, pageable);
            products = productPage.getContent();
        } else {
            // Lấy tất cả sản phẩm nếu không có điều kiện sắp xếp
            productPage = productService.findByStatusTrue(pageable);
            products = productPage.getContent();
        }
        paginationNumber(productPage, page, model);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("productPages", productPage);
        model.addAttribute("totalPages", productPage != null ? productPage.getTotalPages() : 1);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sort", sort);
        model.addAttribute("colors", colors);
        model.addAttribute("sizes", sizes);
        return "site/products/allproduct";
    }

    // lọc theo giá thấp đến giá cao
    @GetMapping("filterMinPriceAndMaxPrice")
    public String filterMinPriceAndMaxPrice(Model model, @RequestParam("minPrice") Optional<Long> minPrice,
            @RequestParam("maxPrice") Optional<Long> maxPrice,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
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
            dataPagination(model, page, size, productsReponse, categoryId);
            return "/site/products/fragments/productlist :: productRows";
        }
        return "/site/products/fragments/productlist :: productRows";
    }

    // lọc theo màu và kích thước
    @GetMapping("filterProduct")
    public String filterSize(Model model, @RequestParam(name = "sizeId", required = false) Integer sizeId,
            @RequestParam(name = "colorId", required = false) Integer colorId,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int sizePage) {
        if (sizeId != null) {
            Optional<Size> sizeOptional = sizeService.findById(sizeId);
            Size size = sizeOptional.get();
            if (sizeOptional.isPresent()) {
                List<ProductVariant> productVariants = productVariantService.findBySize(size, categoryId);
                List<Product> products = productVariants.stream()
                        .map((ProductVariant::getProduct))
                        .distinct() // không có sản phẩm trùng lập
                        .toList();

                dataPagination(model, page, sizePage, products, categoryId);
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

                dataPagination(model, page, sizePage, products, categoryId);
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

                dataPagination(model, page, sizePage, products, categoryId);
            }
        }

        return "/site/products/fragments/productlist :: productRows";
    }

    // lọc combobox
    @GetMapping("sortProduct")
    public String sortProduct(Model model, @RequestParam("typeSort") Integer typeSort,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
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
        dataPagination(model, page, size, products, categoryId);
        return "/site/products/fragments/productlist :: productRows";
    }

    // lọc giá theo range price
    @GetMapping("filterRangePrice")
    public String filterRangePrice(Model model, @RequestParam(name = "price", required = false) BigDecimal price,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        List<Product> products;
        if (price != null) {
            products = productService.findByPriceLessThanEqualAndCategoryId(price, categoryId);
        } else {
            products = productService.findByCategoryId(categoryId);
        }
        dataPagination(model, page, size, products, categoryId);
        return "/site/products/fragments/productlist :: productRows";
    }
}
