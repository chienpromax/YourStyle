package yourstyle.com.shope.controller.admin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.IOException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.model.Discount;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.Size;
import yourstyle.com.shope.service.CategoryService;
import yourstyle.com.shope.service.ColorService;
import yourstyle.com.shope.service.DiscountService;
import yourstyle.com.shope.service.ProductService;
import yourstyle.com.shope.service.SizeService;
import yourstyle.com.shope.utils.UploadUtils;
import yourstyle.com.shope.validation.admin.ProductDto;

@Controller
@RequestMapping("admin/products")
public class ProductsController {

	@Autowired
	CategoryService categoryService;
	@Autowired
	ProductService productService;
	@Autowired
	ColorService colorService;
	@Autowired
	SizeService sizeService;
	@Autowired
	DiscountService discountService;

	@GetMapping("/add")
	public String addProductForm(Model model) {
		List<Category> categories = categoryService.findAll();
		List<Color> colors = colorService.findAll();
		List<Size> sizes = sizeService.findAll();
		List<Discount> discounts = discountService.findAll();

		model.addAttribute("categories", categories);
		model.addAttribute("colors", colors);
		model.addAttribute("color", new Color());
		model.addAttribute("size", new Size());
		model.addAttribute("sizes", sizes);
		model.addAttribute("discount", new Discount());
		model.addAttribute("discounts", discounts);
		model.addAttribute("product", new ProductDto());

		return "admin/products/addOrEdit";
	}

	@GetMapping("edit/{productId}")
	public ModelAndView edit(ModelMap model, @PathVariable("productId") Integer productId) {
		ProductDto productDto = new ProductDto();
		Optional<Product> optional = productService.findById(productId);

		if (optional.isPresent()) {
			Product product = optional.get();
			BeanUtils.copyProperties(product, productDto);
			productDto.setEdit(true);

			List<Discount> discounts = discountService.findByProductId(productId);

			model.addAttribute("product", productDto);
			model.addAttribute("discounts", discounts); // Thêm thông tin giảm giá vào model
			model.addAttribute("categories", categoryService.findAll());
			model.addAttribute("colors", colorService.findAll());
			model.addAttribute("sizes", sizeService.findAll());
			model.addAttribute("color", new Color());
			model.addAttribute("size", new Size());
			model.addAttribute("discount", new Discount());
			model.addAttribute("productId", productId);

			return new ModelAndView("admin/products/addOrEdit", model);
		}

		// Xử lý khi sản phẩm không tồn tại
		model.addAttribute("messageType", "warning");
		model.addAttribute("messageContent", "Sản phẩm không tồn tại!");
		return new ModelAndView("redirect:/admin/products", model);
	}

	@GetMapping("/search")
	public String search(@RequestParam("value") String value,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			Model model) {
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);
		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("name"));

		Page<Product> products = productService.searchByName(value, pageable);

		int totalPages = products.getTotalPages();
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
		model.addAttribute("products", products.getContent());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("size", pageSize);
		model.addAttribute("value", value);

		return "admin/products/list";
	}

	@GetMapping("delete/{productId}")
	public ModelAndView delete(ModelMap model, @PathVariable("productId") Integer productId) {
		Optional<Product> product = productService.findById(productId);
		if (product.isPresent()) {
			productService.deleteById(productId);
			model.addAttribute("messageType", "success");
			model.addAttribute("messageContent", "Xóa thành công");
		} else {
			model.addAttribute("messageType", "error");
			model.addAttribute("messageContent", "Sản phẩm không tồn tại");
		}
		return new ModelAndView("redirect:/admin/products", model);
	}

	private Product convertToProduct(ProductDto productDto) {
		Product product = new Product();
		product.setProductId(productDto.getProductId());
		product.setName(productDto.getName());
		product.setDescription(productDto.getDescription());
		product.setStatus(productDto.getStatus());
		product.setPrice(productDto.getPrice());
		product.setImage(productDto.getImage());
		product.setProductDetail(productDto.getProductDetail());
		product.setCategory(productDto.getCategory());
		return product;
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Validated @ModelAttribute("product") ProductDto productDto,
			BindingResult result, @RequestParam("imageFile") MultipartFile imageFile) {
		Product product = convertToProduct(productDto);
		List<Category> categories = categoryService.findAll();
		model.addAttribute("categories", categories);
		// Kiểm tra lỗi đầu vào
		if (result.hasErrors()) {
			model.addAttribute("productDto", productDto);
			model.addAttribute("messageType", "error");
			model.addAttribute("messageContent", "Lỗi Kiểm tra lại thông tin!");
			return new ModelAndView("admin/products/addOrEdit", model);
		}

		// Kiểm tra file ảnh
		if (imageFile == null || imageFile.isEmpty()) {
			result.rejectValue("imageFile", "error.product", "Vui lòng chọn ảnh đại diện");
		} else if (!imageFile.getContentType().startsWith("image/")) {
			result.rejectValue("imageFile", "error.product", "File không hợp lệ, vui lòng chọn một file ảnh.");
		}

		// Xử lý upload tệp
		if (imageFile != null && !imageFile.isEmpty()) {
			try {
				String uploadDir = "src/main/resources/static/uploads/";
				String originalFilename = imageFile.getOriginalFilename();
				String newFilename = UploadUtils.saveFile(uploadDir, originalFilename, imageFile);
				product.setImage(newFilename);
			} catch (IOException e) {
				model.addAttribute("messageType", "error");
				model.addAttribute("messageContent", "Lỗi khi tải tệp: " + e.getMessage());
				return new ModelAndView("admin/products/addOrEdit", model);
			}
		} else {
			// Giữ nguyên ảnh nếu không có tệp mới tải lên
			if (product.getProductId() != null) {
				Optional<Product> existingProduct = productService.findById(product.getProductId());
				existingProduct.ifPresent(p -> product.setImage(p.getImage()));
			}
		}

		// Xử lý lưu hoặc cập nhật sản phẩm
		if (product.getProductId() != null && productService.findById(product.getProductId()).isPresent()) {
			productService.update(product);
			model.addAttribute("messageType", "success");
			model.addAttribute("messageContent", "Sửa sản phẩm thành công");
		} else {
			productService.save(product);
			model.addAttribute("messageType", "success");
			model.addAttribute("messageContent", "Thêm sản phẩm thành công");
		}

		return new ModelAndView("redirect:/admin/products", model);
	}

	@GetMapping("")
	public String list(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0); // Trang hiện tại
		int pageSize = size.orElse(5); // Kích thước trang

		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("name"));
		Page<Product> list = productService.findAll(pageable);

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

		model.addAttribute("products", list.getContent());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("size", pageSize);

		return "admin/Products/list";
	}

	@PostMapping("toggleStatus/{productId}")
	@ResponseBody
	public ResponseEntity<?> toggleProductStatus(@PathVariable("productId") Integer productId,
			@RequestBody Map<String, Boolean> statusData) {
		try {
			boolean newStatus = statusData.get("status");
			Product product = productService.findById(productId).orElse(null);
			if (product != null) {
				product.setStatus(newStatus); // Cập nhật trạng thái sản phẩm
				productService.save(product);
				return ResponseEntity.ok(Collections.singletonMap("success", true));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("success", false));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Collections.singletonMap("success", false));
		}
	}

}