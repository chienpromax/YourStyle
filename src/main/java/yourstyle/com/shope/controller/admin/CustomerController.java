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
import org.springframework.data.jpa.domain.JpaSort.Path;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.utils.UploadUtils;

@Controller
@RequestMapping("admin/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	@Autowired
    private AccountService accountService;


	@GetMapping("add")
	public String add(Model model) {
		model.addAttribute("customer", new Customer());
		model.addAttribute("customer", new Customer());

        // Lấy danh sách tài khoản và thêm vào model
        List<Account> accounts = accountService.findAll();
        model.addAttribute("accounts", accounts);
		return "admin/customers/addOrEdit";
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Validated @ModelAttribute("customer") Customer customer,
			BindingResult result, @RequestParam("imageFile") MultipartFile imageFile) {
		if (result.hasErrors()) {
			return new ModelAndView("admin/customers/addOrEdit", model);
		}

		// Xử lý upload tệp
		if (imageFile != null && !imageFile.isEmpty()) {
			try {
				String uploadDir = "src/main/resources/static/uploads/";
				String originalFilename = imageFile.getOriginalFilename();
				String newFilename = UploadUtils.saveFile(uploadDir, originalFilename, imageFile);
				customer.setAvatar(newFilename); // Lưu tên tệp vào database
			} catch (IOException e) {
				e.printStackTrace();
				model.addAttribute("message", "Lỗi khi tải tệp: " + e.getMessage());
				return new ModelAndView("admin/customers/addOrEdit", model);
			}
		}

		// Lưu khách hàng vào database
		customerService.save(customer);
		model.addAttribute("message", "Cập nhật khách hàng thành công");
		return new ModelAndView("redirect:/admin/customers", model);
	}

	@GetMapping("")
	public String list(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0); // trang hiện tại
		int pageSize = size.orElse(5); // mặc định hiển thị 5 tài khoản 1 trang
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
		return "admin/Customers/list";
	}
}
