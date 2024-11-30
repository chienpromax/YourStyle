package yourstyle.com.shope.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.repository.AccountRepository;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.CustomerService;
import java.util.Date;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/yourstyle/accounts")
public class UserProfileController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@GetMapping("/profile")
	public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		if (userDetails != null) {
			String username = userDetails.getUsername();
			String email = customerService.getEmailByUsername(username);
			Integer accountId =customerService.getAccountIdByUsername(username);
			String phone = accountService.getPhoneNumberByUsername(username);

			String fullName = accountService.getFullNameByUsername(username);
			Date birthday = accountService.getBirthdayByUsername(username);
			Boolean gender = accountService.getGenderByUsername(username);

			String nationality = accountService.getNationalityByUsername(username);

			String avatarUrl = accountService.getAvatarByUsername(username);

			// debug
			System.out.println("Avatar URL: " + accountService.getAvatarByUsername(username));

			model.addAttribute("email", email);
			model.addAttribute("accountId", accountId);
			model.addAttribute("phoneNumber", phone);
			model.addAttribute("fullName", fullName);
			model.addAttribute("birthday", birthday);
			model.addAttribute("gender", gender);
			model.addAttribute("nationality", nationality);
			model.addAttribute("avatar", avatarUrl); // Truyền Base64 vào model
			System.out.println(
					"hello " + accountId + email + phone + fullName + birthday + gender + nationality + avatarUrl);
		}
		return "site/accounts/personalinformation";
	}

	private boolean isValidEmail(String email) {
		// Định dạng regex cho email hợp lệ
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		return email.matches(emailRegex); // Kiểm tra xem email có khớp với regex không
	}

	@PostMapping("/update-email")
	@ResponseBody
	public ResponseEntity<?> updateEmail(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam String newEmail) {
		Map<String, String> response = new HashMap<>();
		System.out.println("hi " + newEmail);
		if (userDetails != null) {
			String username = userDetails.getUsername();
			if (!isValidEmail(newEmail)) {
				response.put("message", "Email không hợp lệ");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			if (accountService.isEmailExist(newEmail)) {
				response.put("message", "Email này đã tồn tại trong hệ thống");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			// Thực hiện cập nhật email
			boolean updateResult = accountService.updateEmailByUsername(username, newEmail);
			if (updateResult) {
				response.put("message", "Cập nhật email thành công");
				return ResponseEntity.ok(response);
			} else {
				response.put("message", "Tài khoản không tồn tại hoặc không thể cập nhật email");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
		}

		response.put("message", "Thông tin không hợp lệ");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	// Update SDT
	@PostMapping("/update-phone")
	public ResponseEntity<?> updatePhoneNumber(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam String newPhone) {
		Map<String, String> response = new HashMap<>();

		if (userDetails != null) {
			String username = userDetails.getUsername();
			System.out.println("Hi" + username);
			boolean updateResult = accountService.addOrUpdatePhoneNumber(username, newPhone);

			if (updateResult) {
				response.put("message", "Cập nhật số điện thoại thành công");
				return ResponseEntity.ok(response);
			} else {
				response.put("message", "Không thể cập nhật số điện thoại");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
		}

		response.put("message", "Người dùng không hợp lệ");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	// update
	@PostMapping("/update-profile")
	public ResponseEntity<?> updateUserInfo(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam String fullName, @RequestParam String birthday, @RequestParam String gender,
			@RequestParam String nationality, @RequestParam(value = "avatar", required = false) MultipartFile avatar) {

		Map<String, String> response = new HashMap<>();

		if (userDetails != null) {
			String username = userDetails.getUsername();
			LocalDate localDateBirthday = LocalDate.parse(birthday);

			Optional<Account> optionalAccount = accountRepository.findByUsername(username);
			if (!optionalAccount.isPresent()) {
				response.put("message", "Không tìm thấy tài khoản");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			Account account = optionalAccount.get();
			Integer accountId = account.getAccountId();

			Optional<Customer> optionalCustomer = customerRepository.findOptionalByAccount_AccountId(accountId);

			if (!optionalCustomer.isPresent()) {
				response.put("message", "Không tìm thấy thông tin người dùng");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			Customer customer = optionalCustomer.get();

			boolean updateResult = accountService.updateUserInfo(username, fullName, localDateBirthday, gender,
					nationality, avatar);

			if (updateResult) {
				String avatarUrl = customer.getAvatar();
				response.put("message", "Cập nhật thông tin thành công");
				response.put("avatar", avatarUrl); 
				return ResponseEntity.ok(response);
			} else {
				response.put("message", "Không thể cập nhật thông tin");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
		}

		response.put("message", "Người dùng không hợp lệ");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

}
