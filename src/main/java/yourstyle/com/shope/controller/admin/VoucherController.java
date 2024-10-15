package yourstyle.com.shope.controller.admin;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;

import yourstyle.com.shope.model.Voucher;
import yourstyle.com.shope.service.VoucherService;

@Controller
@RequestMapping("admin/vouchers")
public class VoucherController {
    @Autowired
    VoucherService voucherService;

    @GetMapping("add")
    public String add(Model model) {
        Voucher voucher = new Voucher();
        voucher.setIsPublic(true);
        model.addAttribute("voucher", voucher);
        return "admin/vouchers/addOrEdit";
    }

    @PostMapping("saveOrUpdate")
    public ModelAndView saveOrUpdate(ModelMap model, @ModelAttribute("voucher") Voucher voucher) {
        voucher.setCreateBy(2);

        // Kiểm tra và in ra để đảm bảo thời gian được chọn đúng
        System.out.println("Start Date: " + voucher.getStartDate());
        System.out.println("End Date: " + voucher.getEndDate());

        voucherService.createVoucher(voucher);

        return new ModelAndView("redirect:/admin/vouchers", model);
    }

    @GetMapping("")
    public String list(Model model, @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(0); // trang hiện tại
        int pageSize = size.orElse(5); // mặc định hiển thị 5 tài khoản 1 trang
        Pageable pageable = PageRequest.of(currentPage, pageSize,
                Sort.by("voucherCode"));
        Page<Voucher> list = voucherService.findAll(pageable);
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
            List<Integer> pageNumbers = IntStream.rangeClosed(start,
                    end).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("vouchers", list);
        return "admin/vouchers/list";
    }
}
