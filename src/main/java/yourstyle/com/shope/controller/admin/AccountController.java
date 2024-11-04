package yourstyle.com.shope.controller.admin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import jakarta.validation.Valid;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Role;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.RoleService;
import yourstyle.com.shope.validation.admin.AccountDto;

@Controller
@RequestMapping("admin/accounts")
public class AccountController {
	@Autowired
	AccountService accountService;
	@Autowired
	RoleService roleService;
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("add")
	public String add(Model model) {
		List<Role> roles = roleService.findAll();
		model.addAttribute("account", new AccountDto());
		model.addAttribute("roles", roles);
		return "admin/accounts/addOrEdit";
	}

	@GetMapping("edit/{accountId}")
	public ModelAndView edit(ModelMap model, @PathVariable("accountId") Integer accountId) {
		AccountDto accountDto = new AccountDto();
		Optional<Account> optional = accountService.findById(accountId);
		List<Role> roles = roleService.findAll();
		if (optional.isPresent()) { // tồn tại
			Account account = optional.get();
			BeanUtils.copyProperties(account, accountDto);
			accountDto.setPassword(""); // Để trống mật khẩu khi chỉnh sửa
			accountDto.setEdit(true);
			model.addAttribute("account", accountDto);
			model.addAttribute("roles", roles);
			return new ModelAndView("admin/accounts/addOrEdit");
		}
		model.addAttribute("messageType", "warning");
		model.addAttribute("messageContent", "Tài khoản không tồn tại!");
		return new ModelAndView("redirect:/admin/accounts", model);
	}
	

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("account") AccountDto accountDto,
			BindingResult result, @RequestParam(name = "roleId", required = true) Integer roleId) {
		if (result.hasErrors()) {
			List<Role> roles = roleService.findAll();
			model.addAttribute("roles", roles);
			return new ModelAndView("admin/accounts/addOrEdit");
		}
	
		Account existingAccount = accountService.findByUsernameOrEmail(accountDto.getUsername(), accountDto.getEmail());
	
		if (existingAccount != null && !existingAccount.getAccountId().equals(accountDto.getAccountId())) {
			if (existingAccount.getEmail().equals(accountDto.getEmail())) {
				model.addAttribute("messageType", "error");
				model.addAttribute("messageContent", "Email đã được sử dụng!");
				return new ModelAndView("redirect:/admin/accounts/add", model);
			}
			model.addAttribute("messageType", "error");
			model.addAttribute("messageContent", "Tên tài khoản đã tồn tại!");
			return new ModelAndView("redirect:/admin/accounts/add", model);
		}
	
		Account account = new Account();
		Role role = roleService.findById(roleId).orElse(null);
		accountDto.setRole(role);
	
		BeanUtils.copyProperties(accountDto, account);
		boolean isNew = accountDto.getAccountId() == null;
	
		if (isNew || (accountDto.getPassword() != null && !accountDto.getPassword().isEmpty())) {
			account.setPassword(bCryptPasswordEncoder.encode(accountDto.getPassword()));
		} else {
			account.setPassword(existingAccount.getPassword());
		}
	
		accountService.save(account);
	
		model.addAttribute("messageType", "success");
		model.addAttribute("messageContent", isNew ? "Thêm tài khoản thành công" : "Cập nhật tài khoản thành công");
	
		return new ModelAndView("redirect:/admin/accounts", model);
	}
	
	
	@GetMapping("")
	public String list(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0); // trang hiện tại
		int pageSize = size.orElse(5); // mặc định hiển thị 5 tài khoản 1 trang
		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("username"));
		Page<Account> list = accountService.findAll(pageable);
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
		model.addAttribute("accounts", list);
		return "admin/accounts/list";
	}

	@GetMapping("search")
	public String searchAccount(Model model, @RequestParam(name = "value", required = false) String value,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(0); // trang hiện tại
		int pageSize = size.orElse(5); // mặc định hiển thị 5 tài khoản 1 trang
		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("username"));
		Page<Account> list = null;
		if (StringUtils.hasText(value)) {
			list = accountService.findByUsernameOrEmail(value, pageable);
		} else {
			list = accountService.findAll(pageable);
		}
		int totalPages = list.getTotalPages(); // lấy tổng số trang
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
		model.addAttribute("accounts", list);
		return "admin/accounts/list";
	}

	@PostMapping("toggleStatus/{accountId}")
	@ResponseBody
	public ResponseEntity<?> toggleAccountStatus(@PathVariable("accountId") Integer accountId,
			@RequestBody Map<String, Boolean> statusData) {
		try {
			boolean newStatus = statusData.get("status");
			Account account = accountService.findById(accountId).get();
			if (account != null) {
				account.setStatus(newStatus);
				accountService.save(account);
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
