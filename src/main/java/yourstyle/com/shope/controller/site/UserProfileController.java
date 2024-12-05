package yourstyle.com.shope.controller.site;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.AddressService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.utils.UploadUtils;
import yourstyle.com.shope.validation.admin.CustomerDto;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/yourstyle/accounts")
public class UserProfileController {

	@Autowired
	private AccountService accountService;
	@Autowired
	AddressService addressService;
	@Autowired
	CustomerService customerService;

	@GetMapping("profile")
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
			model.addAttribute("defaultAddress", defaultAddress);
			model.addAttribute("type", type);

			// Kiểm tra vai trò của tài khoản để thiết lập trang điều hướng
			if (customer.getAccount() != null && customer.getAccount().getRole() != null) {
				String roleName = customer.getAccount().getRole().getName();
				model.addAttribute("redirectUrl", "/site/accounts/personalinformation.html");
			}

			return new ModelAndView("/site/accounts/personalinformation.html", model);
		}

		model.addAttribute("messageType", "warning");
		model.addAttribute("messageContent", "Người dùng không tồn tại!");
		return new ModelAndView("redirect:/site/accounts/personalinformation.html", model);
	}

	@PostMapping("profile")
	public ModelAndView updateCustomerInfo(@ModelAttribute("customer") CustomerDto customerDto,
			@RequestParam MultipartFile imageFile, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Optional<Customer> optional = customerService.findById(customerDto.getCustomerId());
			if (optional.isPresent()) {
				Customer customer = optional.get();
				BeanUtils.copyProperties(customerDto, customer);
				if (imageFile != null && !imageFile.isEmpty()) {
					try {
						String uploadDir = "src/main/resources/static/uploads/";
						String originalFilename = imageFile.getOriginalFilename();
						String newFilename = UploadUtils.saveFile(uploadDir, originalFilename, imageFile);
						customer.setAvatar(newFilename); // Gán tên tệp mới vào đối tượng customer
					} catch (IOException e) {
						model.addAttribute("messageType", "error");
						model.addAttribute("messageContent", "Lỗi khi tải tệp: " + e.getMessage());
					}
				}
				// Lưu thông tin mới
				customerService.save(customer);

				redirectAttributes.addFlashAttribute("messageType", "success");
				redirectAttributes.addFlashAttribute("messageContent", "Cập nhật thông tin thành công!");
			} else {
				redirectAttributes.addFlashAttribute("messageType", "error");
				redirectAttributes.addFlashAttribute("messageContent", "Không tìm thấy khách hàng để cập nhật.");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("messageType", "error");
			redirectAttributes.addFlashAttribute("messageContent", "Đã xảy ra lỗi khi cập nhật thông tin.");
		}

		return new ModelAndView(
				"redirect:/yourstyle/accounts/profile?id=" + customerDto.getCustomerId() + "&type=customer");
	}

}