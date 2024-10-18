package yourstyle.com.shope.controller.admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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

        // model.addAttribute("customers", listCustomers());
        model.addAttribute("voucher", voucher);
        return "admin/vouchers/addOrEdit";
    }

    // public List<Customer> listCustomers() {
    // List<Customer> customers = new ArrayList<>();
    // customers.add(new Customer(1, "Althan Travis", "0935141828", true,
    // "09-01-2001"));
    // customers.add(new Customer(2, "John Doe", "0901234567", false,
    // "22-05-1995"));
    // customers.add(new Customer(3, "Jane Smith", "0987654321", true,
    // "14-07-1998"));

    // return customers;
    // }

    @GetMapping("edit/{voucherId}")
    public ModelAndView edit(ModelMap model, @PathVariable("voucherId") Integer voucherId) {
        Optional<Voucher> optional = voucherService.findByVoucherId(voucherId);

        if (optional.isPresent()) {
            Voucher voucher = optional.get(); // Lấy dữ liệu nếu tồn tại
            model.addAttribute("voucher", voucher); // Thêm vào model với tên "voucher"
            return new ModelAndView("admin/vouchers/addOrEdit", model); // Trả về view để chỉnh sửa
        }

        // Thêm thông báo lỗi nếu không tìm thấy voucher
        model.addAttribute("messageType", "warning");
        model.addAttribute("messageContent", "Voucher không tồn tại!");
        return new ModelAndView("redirect:/admin/vouchers", model); // Redirect với thông báo
    }

    @PostMapping("saveOrUpdate")
    public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("voucher") Voucher voucher,
            BindingResult result) {
        voucher.setCreateBy(2); // Thiết lập người tạo

        // Kiểm tra lỗi dữ liệu
        if (result.hasErrors()) {
            // Nếu có lỗi, quay lại form nhập liệu
            return new ModelAndView("admin/vouchers/addOrEdit", model);
        }

        // Kiểm tra xem voucherId có null không
        if (voucher.getVoucherId() != null) {
            // Nếu voucherId không null, kiểm tra voucher trong DB
            Optional<Voucher> existingVoucherOpt = voucherService.findByVoucherId(voucher.getVoucherId());

            if (existingVoucherOpt.isPresent()) {
                // Nếu đã tồn tại, cập nhật thông tin
                Voucher existingVoucher = existingVoucherOpt.get();
                BeanUtils.copyProperties(voucher, existingVoucher, "voucherId"); // Bỏ qua voucherId khi copy
                voucherService.createVoucher(existingVoucher);
                model.addAttribute("messageType", "success");
                model.addAttribute("messageContent", "Cập nhật voucher thành công");
            } else {
                model.addAttribute("messageType", "error");
                model.addAttribute("messageContent", "Voucher không tồn tại.");
                return new ModelAndView("admin/vouchers/addOrEdit", model);
            }
        } else {
            // Nếu voucherId là null, thêm mới voucher
            voucherService.createVoucher(voucher);
            model.addAttribute("messageType", "success");
            model.addAttribute("messageContent", "Thêm voucher thành công");
        }

        // Chuyển hướng về danh sách voucher
        return new ModelAndView("redirect:/admin/vouchers", model);
    }

    @GetMapping("")
    public String list(Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(0); // trang hiện tại
        int pageSize = size.orElse(5); // Số hàng

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

    @GetMapping("search")
    public String searchVoucher(Model model,
            @RequestParam(name = "value", required = false) String value,
            @RequestParam(name = "isPublic", required = false) Boolean isPublic,
            @RequestParam(name = "type", required = false) Integer type,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime toDate,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(0);
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("voucherCode"));

        Page<Voucher> list;
        if (StringUtils.hasText(value) || isPublic != null || type != null || fromDate != null || toDate != null) {
            list = voucherService.advancedSearch(value, isPublic, type, fromDate, toDate, pageable);
        } else {
            list = voucherService.findAll(pageable);
        }

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

        model.addAttribute("vouchers", list);
        model.addAttribute("searchValue", value);
        model.addAttribute("isPublic", isPublic);
        model.addAttribute("type", type);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "admin/vouchers/list";
    }

    @GetMapping("delete/{voucherId}")
    public String getMethodName(@PathVariable("voucherId") Integer voucherId) {
        voucherService.deleteByVoucherId(voucherId);
        return "redirect:/admin/vouchers";
    }

}
