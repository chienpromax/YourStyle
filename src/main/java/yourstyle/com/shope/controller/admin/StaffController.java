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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.repository.AccountRepository;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.validation.admin.StaffDto;

@Controller
@RequestMapping("admin/staffs")
public class StaffController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @GetMapping("add")
    public String add(String type, Model model) {
        List<Account> accounts = accountService.findAll();
        model.addAttribute("isEdit", false);
        model.addAttribute("type", type);
        model.addAttribute("accounts", accounts);
        model.addAttribute("staff", new StaffDto());
        return "admin/customers/addOrEdit";
    }

    @GetMapping("/search")
    public String searchStaffs(
            @RequestParam(value = "value", required = false, defaultValue = "") String value,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            Model model) {

        // Pageable configuration
        Pageable pageable = PageRequest.of(page, size);

        // Retrieve employees (ROLE_EMPLOYEE)
        Page<Customer> customers = customerService.searchByNameOrPhoneStaff(value, pageable);

        // Add attributes to the model
        model.addAttribute("staffs", customers.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customers.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("value", value);

        return "admin/staffs/list"; // Đường dẫn tới file HTML Thymeleaf
    }

    @GetMapping("delete/{customerId}")
    public ModelAndView delete(ModelMap model, @PathVariable("customerId") Integer customerId, RedirectAttributes redirectAttribute) {
        Optional<Customer> customer = customerService.findById(customerId);
        if (customer.isPresent()) {
            try {
                customerService.deleteById(customerId);
                redirectAttribute.addAttribute("messageType", "success");
                redirectAttribute.addAttribute("messageContent", "Xóa thành công");
            } catch (Exception e) {
                redirectAttribute.addAttribute("messageType", "error");
                redirectAttribute.addAttribute("messageContent", "Lỗi khi xóa nhân viên có rằng buộc:");
            }
        } else {
            redirectAttribute.addAttribute("messageType", "error");
            redirectAttribute.addAttribute("messageContent", "Không tìm thấy khách hàng để xóa.");
        }
        return new ModelAndView("redirect:/admin/staffs");
    }
    

    @GetMapping("")
    public String list(Model model, @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(0); // Trang hiện tại
        int pageSize = size.orElse(5); // Kích thước trang

        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("fullname"));
        Page<Customer> customers = customerService.findAllStaff("ROLE_EMPLOYEE", pageable); // Lấy danh sách nhân viên

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

        model.addAttribute("staffs", customers.getContent()); // Truyền danh sách nhân viên tới giao diện
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", pageSize);

        return "admin/staffs/list";
    }

}
