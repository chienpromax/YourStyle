package yourstyle.com.shope.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.service.CategoryService;
import yourstyle.com.shope.service.ProductService;
import yourstyle.com.shope.utils.UploadUtils;
import yourstyle.com.shope.validation.admin.ProductDto;

@Controller
@RequestMapping("admin/products")
public class ProductsController {

	@Autowired
	CategoryService categoryService;

	@Autowired
	ProductService productService;

	@GetMapping("add")
	public String add(Model model) {
		List<Category> categories = categoryService.findAll();
		model.addAttribute("categories", categories);
		model.addAttribute("product", new ProductDto());
		return "admin/products/addOrEdit";
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
		System.out.println(categories);
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

		model.addAttribute("Products", list.getContent());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("size", pageSize);

		return "admin/Products/list";
	}

}

// @GetMapping("/search")
// public String search(@RequestParam("value") String value,
// @RequestParam("page") Optional<Integer> page,
// @RequestParam("size") Optional<Integer> size,
// Model model) {
// int currentPage = page.orElse(0);
// int pageSize = size.orElse(5);
// Pageable pageable = PageRequest.of(currentPage, pageSize,
// Sort.by("fullname"));

// // Gọi phương thức tìm kiếm hỗ trợ phân trang
// Page<Customer> customers = customerService.searchByNameOrPhone(value,
// pageable);

// int totalPages = customers.getTotalPages();
// if (totalPages > 0) {
// int start = Math.max(1, currentPage + 1 - 2);
// int end = Math.min(currentPage + 1 + 2, totalPages);
// if (totalPages > 5) {
// if (end == totalPages) {
// start = end - 5;
// } else if (start == 1) {
// end = start + 5;
// }
// }
// List<Integer> pageNumbers = IntStream.rangeClosed(start,
// end).boxed().collect(Collectors.toList());
// model.addAttribute("pageNumbers", pageNumbers);
// }

// model.addAttribute("Customers", customers.getContent());
// model.addAttribute("currentPage", currentPage);
// model.addAttribute("size", pageSize);
// model.addAttribute("value", value);

// return "admin/Customers/list";
// }

// @GetMapping("delete/{customerId}")
// public ModelAndView delete(ModelMap model, @PathVariable("customerId")
// Integer customerId) {
// Optional<Customer> customer = customerService.findById(customerId);
// if (customer.isPresent()) {
// customerService.deleteById(customerId);
// model.addAttribute("messageType", "success");
// model.addAttribute("messageContent", "Xóa thành công");
// } else {
// model.addAttribute("messageType", "error");
// model.addAttribute("messageContent", "Khách hàng không tồn tại");
// }
// return new ModelAndView("redirect:/admin/customers", model);
// }

// @GetMapping("edit/{customerId}")
// public ModelAndView edit(ModelMap model, @PathVariable("customerId") Integer
// customerId) {
// CustomerDto customerDto = new CustomerDto();
// Optional<Customer> optional = customerService.findById(customerId);
// List<Account> accounts = accountService.findAll();
// if (optional.isPresent()) { // tồn tại
// Customer customer = optional.get();
// BeanUtils.copyProperties(customer, customerDto);
// customerDto.setEdit(true);
// model.addAttribute("customer", customerDto);
// model.addAttribute("accounts", accounts);
// return new ModelAndView("admin/customers/addOrEdit");
// }
// model.addAttribute("messageType", "warning");
// model.addAttribute("messageContent", "người dùng không tồn tại!");
// return new ModelAndView("redirect:/admin/customers", model);
// }
