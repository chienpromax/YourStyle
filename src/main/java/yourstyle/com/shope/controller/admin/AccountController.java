package yourstyle.com.shope.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.service.AccountService;

@Controller
@RequestMapping("admin/accounts")
public class AccountController {
	@Autowired
	AccountService accountService;
	
    @GetMapping("add")
    public String add() {
        return "admin/accounts/addOrEdit";
    }
    
    @GetMapping("")
    public String list(Model model,@RequestParam("page") Optional<Integer> page,
    		@RequestParam("size") Optional<Integer> size) {
    	int currentPage = page.orElse(0); // trang hiện tại
    	int pageSize = size.orElse(5); // mặc định hiển thị 5 tài khoản 1 trang
    	Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("username"));
    	Page<Account> list =  accountService.findAll(pageable);
    	int totalPages = list.getTotalPages();
    	if (totalPages > 0) {
			int start = Math.max(1, currentPage + 1 -2);
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
    public String searchAccount(Model model,@RequestParam(name = "value",required = false) String value,
    		@RequestParam("page") Optional<Integer> page,@RequestParam("size") Optional<Integer> size) {
    	int currentPage = page.orElse(0); // trang hiện tại
    	int pageSize = size.orElse(5); // mặc định hiển thị 5 tài khoản 1 trang
    	Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("username"));
    	Page<Account> list = null;
    	if (StringUtils.hasText(value)) {
			list = accountService.findByUsernameOrEmail(value,pageable);
		}else {
			list = accountService.findAll(pageable);
		}
    	int totalPages = list.getTotalPages();
    	if (totalPages > 0) {
			int start = Math.max(1, currentPage + 1 -2);
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
}
