package yourstyle.com.shope.controller.admin;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.AddressService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.utils.UploadUtils;
import yourstyle.com.shope.validation.admin.CustomerDto;

@Controller
@RequestMapping("admin/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private AddressService addressService;

	@GetMapping("add")
	public String add(String type, Model model) {
		List<Account> accounts = accountService.findAll();
		model.addAttribute("isEdit", false);
		model.addAttribute("type", type);
		model.addAttribute("accounts", accounts);
		model.addAttribute("customer", new CustomerDto());
		return "admin/customers/addOrEdit";
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Validated @ModelAttribute("customer") CustomerDto customerDto,
			BindingResult result, @RequestParam MultipartFile imageFile,
			@RequestParam(required = false, defaultValue = "customer") String type,
			RedirectAttributes redirectAttributes) {

		List<Account> accounts = accountService.findAll();
		model.addAttribute("accounts", accounts);

		Customer customer = convertToCustomer(customerDto);

		// Address address = convertToAddress(customerDto); // Convert DTO thành Address
		// FIX
		Address address = addressService.findDefaultByCustomerId(customer.getCustomerId()).orElse(new Address());
		BeanUtils.copyProperties(customerDto, address);
		// END FIX

		if (result.hasErrors()) {
			redirectAttributes.addAttribute("customer", customerDto);
			redirectAttributes.addAttribute("messageType", "error");
			redirectAttributes.addAttribute("messageContent", "Lỗi Kiểm tra lại thông tin!");
			return new ModelAndView("admin/customers/addOrEdit", model);
		}

		// Upload và xử lý ảnh đại diện
		if (imageFile != null && !imageFile.isEmpty()) {
			try {
				String uploadDir = "src/main/resources/static/uploads/";
				String originalFilename = imageFile.getOriginalFilename();
				String newFilename = UploadUtils.saveFile(uploadDir, originalFilename, imageFile);
				customer.setAvatar(newFilename);
			} catch (IOException e) {
				model.addAttribute("messageType", "error");
				model.addAttribute("messageContent", "Lỗi khi tải tệp: " + e.getMessage());
				return new ModelAndView("admin/customers/addOrEdit", model);
			}
		}

		// Lưu hoặc cập nhật thông tin
		if (customer.getCustomerId() != null) {
			customerService.update(customer);
			addressService.updateDefaultAddress(address, customer.getCustomerId());
			redirectAttributes.addAttribute("messageType", "success");
			redirectAttributes.addAttribute("messageContent", "Cập nhật thành công!");

		} else {
			Customer savedCustomer = customerService.save(customer);
			address.setCustomer(savedCustomer);
			address.setIsDefault(true);
			addressService.save(address);
			redirectAttributes.addAttribute("messageType", "success");
			redirectAttributes.addAttribute("messageContent", "Thêm thành công!");

		}

		// String redirectUrl = "/admin/customers"; // Mặc định chuyển hướng đến trang customers
		// if (customer.getAccount() != null && customer.getAccount().getRole() != null) {
		// 	// Debug thông tin vai trò
		// 	System.out.println("Role name: " + customer.getAccount().getRole().getName());
		// 	boolean isEmployee = "ROLE_EMPLOYEE".equals(customer.getAccount().getRole().getName());
		// 	if (isEmployee) {
		// 		redirectUrl = "/admin/staffs"; // Nếu là employee, chuyển hướng đến trang staffs
		// 	}
		// } else {
		// 	System.out.println("Account or Role is null");
		// }

		// Điều hướng đến trang phù hợp
		String redirectUrl = type.equals("staff") ? "/admin/staffs" :
		"/admin/customers";
		return new ModelAndView("redirect:" + redirectUrl);

	}

	@GetMapping("edit")
	public ModelAndView edit(ModelMap model,
			@RequestParam("id") Integer customerId,
			@RequestParam String type) {
		CustomerDto customerDto = new CustomerDto();
		Optional<Customer> optional = customerService.findById(customerId);
		List<Account> accounts = accountService.findAll();

		if (optional.isPresent()) {
			Customer customer = optional.get();
			BeanUtils.copyProperties(customer, customerDto);
			customerDto.setEdit(true);

			// Lấy địa chỉ mặc định
			Optional<Address> defaultAddressOpt = addressService.findDefaultByCustomerId(customerId);
			Address defaultAddress = defaultAddressOpt.orElse(new Address());

			// Thiết lập địa chỉ mặc định cho customerDto
			customerDto.setStreet(defaultAddress.getStreet());
			customerDto.setCity(defaultAddress.getCity());
			customerDto.setDistrict(defaultAddress.getDistrict());
			customerDto.setWard(defaultAddress.getWard());

			model.addAttribute("customer", customerDto);
			model.addAttribute("accounts", accounts);
			model.addAttribute("type", type); // Thêm type vào model
			return new ModelAndView("admin/customers/addOrEdit");
		}
		model.addAttribute("messageType", "warning");
		model.addAttribute("messageContent", "Người dùng không tồn tại!");
		return new ModelAndView("redirect:/admin/customers", model);
	}

	private Address convertToAddress(CustomerDto customerDto) {
		Address address = new Address();
		address.setStreet(customerDto.getStreet());
		address.setCity(customerDto.getCity());
		address.setDistrict(customerDto.getDistrict());
		address.setWard(customerDto.getWard());
		address.setIsDefault(customerDto.getIsDefault() != null ? customerDto.getIsDefault() : false);
		return address;
	}

	@GetMapping("/search")
	public String search(@RequestParam("value") String value,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size,
			Model model) {
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(5);
		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("fullname"));

		// Gọi phương thức tìm kiếm hỗ trợ phân trang
		Page<Customer> customers = customerService.searchByNameOrPhone(value, pageable);

		int totalPages = customers.getTotalPages();
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

		model.addAttribute("Customers", customers.getContent());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("size", pageSize);
		model.addAttribute("value", value);

		return "admin/Customers/list";
	}

	@GetMapping("delete/{customerId}")
	public ModelAndView delete(ModelMap model, @PathVariable("customerId") Integer customerId) {
		Optional<Customer> customer = customerService.findById(customerId);
		if (customer.isPresent()) {
			customerService.deleteById(customerId);
			model.addAttribute("messageType", "success");
			model.addAttribute("messageContent", "Xóa thành công");
		} else {
			model.addAttribute("messageType", "error");
			model.addAttribute("messageContent", "Khách hàng không tồn tại");
		}
		return new ModelAndView("redirect:/admin/customers", model);
	}

	// Định nghĩa phương thức chuyển đổi từ CustomerDto sang Customer
	private Customer convertToCustomer(CustomerDto customerDto) {
		Customer customer = new Customer();
		customer.setCustomerId(customerDto.getCustomerId());
		customer.setFullname(customerDto.getFullname());
		customer.setPhoneNumber(customerDto.getPhoneNumber());
		customer.setGender(customerDto.getGender());
		customer.setAvatar(customerDto.getAvatar());
		customer.setBirthday(customerDto.getBirthday());
		customer.setAccount(customerDto.getAccount());
		return customer;
	}

	@GetMapping("")
	public String list(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0); // Trang hiện tại
		int pageSize = size.orElse(5); // Kích thước trang

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

		return "admin/Customers/list";
	}

}