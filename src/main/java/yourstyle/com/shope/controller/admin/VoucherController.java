package yourstyle.com.shope.controller.admin;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Voucher;
import yourstyle.com.shope.model.VoucherCustomer;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.VoucherCustomerService;
import yourstyle.com.shope.service.VoucherService;
import yourstyle.com.shope.validation.admin.VoucherDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
@RequestMapping("admin/vouchers")
public class VoucherController {
    @Autowired
    VoucherService voucherService;

    @Autowired
    CustomerService customerService;

    @Autowired
    VoucherCustomerService voucherCustomerService;

    @GetMapping("add")
    public String add(Model model) {
        VoucherDTO voucherDTO = new VoucherDTO();
        voucherDTO.setIsPublic(true);

        model.addAttribute("voucher", voucherDTO);
        return "admin/vouchers/addOrEdit";
    }

    @GetMapping("edit/{voucherId}")
    public ModelAndView edit(ModelMap model,
            @PathVariable("voucherId") Integer voucherId) {
        // Lấy voucher theo voucherId
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

    @GetMapping("deleteCustomer/{voucherId}/{customerId}")
    public String deleteCustomerOfVoucher(@PathVariable("customerId") Integer customerId,
            @PathVariable("voucherId") Integer voucherId) {
        // Xóa bản ghi trong bảng VoucherCustomer theo voucherId và customerId
        voucherCustomerService.deleteByCustomerIdAndVoucherId(customerId, voucherId);

        // Sau khi xóa, chuyển hướng về trang voucher với thông tin voucherId hiện tại
        return "redirect:/admin/vouchers/addCustomer/" + voucherId;
    }

    @GetMapping("addCustomer/{voucherId}")
    public ModelAndView addCustomer(ModelMap model,
            @RequestParam(value = "value", required = false) String value,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @PathVariable("voucherId") Integer voucherId,
            @RequestParam(value = "selectedCustomers", required = false) List<Integer> selectedIds) {

        Optional<Voucher> optional = voucherService.findByVoucherId(voucherId);
        if (optional.isPresent()) {
            Voucher voucher = optional.get();
            model.addAttribute("voucher", voucher);

            int currentPage = page.orElse(0);
            int pageSize = size.orElse(10);

            Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("fullname"));
            Page<Customer> customers = value != null && !value.trim().isEmpty()
                    ? customerService.searchByNameOrPhone(value, pageable)
                    : customerService.findAll(pageable);

            int totalPages = customers.getTotalPages();
            List<Integer> pageNumbers = IntStream.rangeClosed(
                    Math.max(1, currentPage + 1 - 2),
                    Math.min(currentPage + 1 + 2, totalPages)).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);

            // Duy trì danh sách khách hàng đã chọn
            if (selectedIds == null) {
                selectedIds = voucher.getVoucherCustomers()
                        .stream()
                        .map(vc -> vc.getCustomer().getCustomerId())
                        .collect(Collectors.toList());
            }
            model.addAttribute("selectedCustomerIds", selectedIds);

            model.addAttribute("customers", customers.getContent());
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("size", pageSize);
            model.addAttribute("value", value);

            return new ModelAndView("admin/vouchers/voucherCustomer", model);
        }

        model.addAttribute("messageType", "warning");
        model.addAttribute("messageContent", "Voucher không tồn tại!");
        return new ModelAndView("redirect:/admin/vouchers", model);
    }

    @PostMapping("saveOrUpdateCustomer")
    public ModelAndView saveOrUpdateCustomer(ModelMap model,
            @ModelAttribute("voucher") Voucher voucher,
            @RequestParam(value = "selectedCustomers", required = false) List<Integer> selectedIds) {

        // Nếu không có customer nào được chọn, khởi tạo danh sách rỗng
        if (selectedIds == null) {
            selectedIds = new ArrayList<>();
        }

        // Kiểm tra nếu voucher đã tồn tại trong database
        Optional<Voucher> optionalVoucher = voucherService.findByVoucherId(voucher.getVoucherId());
        if (optionalVoucher.isEmpty()) {
            model.addAttribute("messageType", "warning");
            model.addAttribute("messageContent", "Voucher không tồn tại!");
            return new ModelAndView("redirect:/admin/vouchers", model);
        }

        Voucher voucherForCustomer = optionalVoucher.get(); // Dùng voucher từ database

        // Lưu các khách hàng mới
        for (Integer customerId : selectedIds) {
            boolean exists = voucherCustomerService.existsByCustomerIdAndVoucherId(customerId,
                    voucherForCustomer.getVoucherId());

            // Nếu customer chưa có trong voucher, thêm vào
            if (!exists) {
                VoucherCustomer voucherCustomer = new VoucherCustomer();
                Optional<Customer> customer = customerService.findById(customerId);

                // Sử dụng ifPresent để tránh lỗi redeclaration
                customer.ifPresent(cust -> {
                    voucherCustomer.setVoucher(voucherForCustomer); // Thiết lập voucher
                    voucherCustomer.setCustomer(cust); // Thiết lập customer
                    voucherCustomerService.save(voucherCustomer); // Lưu voucherCustomer
                });
            }
        }

        model.addAttribute("messageType", "success");
        model.addAttribute("messageContent", "Khách hàng đã được thêm thành công!");
        return new ModelAndView("redirect:/admin/vouchers/addCustomer/" + voucherForCustomer.getVoucherId(), model);

    }

    @PostMapping("saveOrUpdate")
    public ModelAndView saveOrUpdate(ModelMap model,
            @Valid @ModelAttribute("voucher") VoucherDTO voucherDTO,
            BindingResult result) {

        Account account = new Account();
        account.setAccountId(2);
        voucherDTO.setAccount(account); // Thiết lập người tạo

        // Kiểm tra lỗi dữ liệu từ DTO
        if (result.hasErrors()) {
            // Nếu có lỗi, lấy danh sách khách hàng và thêm vào model
            List<Customer> customers = customerService.findAll(); // Lấy danh sách khách hàng từ service
            model.addAttribute("customers", customers); // Thêm danh sách khách hàng vào model
            return new ModelAndView("admin/vouchers/addOrEdit", model);
        }

        // Khởi tạo đối tượng Voucher để lưu vào DB
        Voucher voucher = new Voucher();
        BeanUtils.copyProperties(voucherDTO, voucher); // Copy dữ liệu từ DTO sang entity

        // Khởi tạo voucher cho vouchercustomer
        Voucher voucherForCustomer = new Voucher();

        // Kiểm tra xem voucherId có null không
        if (voucherDTO.getVoucherId() != null) {
            // Nếu voucherId không null, kiểm tra voucher trong DB
            Optional<Voucher> existingVoucherOpt = voucherService.findByVoucherId(voucherDTO.getVoucherId());

            if (existingVoucherOpt.isPresent()) {
                // Nếu đã tồn tại, cập nhật thông tin
                Voucher existingVoucher = existingVoucherOpt.get();
                BeanUtils.copyProperties(voucherDTO, existingVoucher, "voucherId"); // Bỏ qua voucherId khi copy

                voucherForCustomer = voucherService.createVoucher(existingVoucher); // Cập nhật voucher
                model.addAttribute("messageType", "success");
                model.addAttribute("messageContent", "Cập nhật voucher thành công");
            } else {
                model.addAttribute("messageType", "error");
                model.addAttribute("messageContent", "Voucher không tồn tại.");
                return new ModelAndView("admin/vouchers/addOrEdit", model);
            }
        } else {
            // Nếu voucherId là null, thêm mới voucher
            voucherForCustomer = voucherService.createVoucher(voucher); // Thêm mới voucher
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

        // Xóa tất cả các bản ghi liên quan trong VoucherCustomer trước
        voucherCustomerService.deleteByVoucherId(voucherId);

        voucherService.deleteByVoucherId(voucherId);
        return "redirect:/admin/vouchers";
    }

    @GetMapping("export/excel")
    public ResponseEntity<byte[]> exportVouchersToExcel() throws IOException {
        // Tạo workbook mới
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Vouchers");

        // Tạo header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Voucher ID");
        headerRow.createCell(1).setCellValue("Voucher Code");
        headerRow.createCell(2).setCellValue("Voucher Name");
        headerRow.createCell(3).setCellValue("Description");
        headerRow.createCell(4).setCellValue("Discount Amount");
        headerRow.createCell(5).setCellValue("Min Total Amount");
        headerRow.createCell(6).setCellValue("Max Total Amount");
        headerRow.createCell(7).setCellValue("Max Uses");
        headerRow.createCell(8).setCellValue("Max Uses User");
        headerRow.createCell(9).setCellValue("Type");
        headerRow.createCell(10).setCellValue("Start Date");
        headerRow.createCell(11).setCellValue("End Date");
        headerRow.createCell(12).setCellValue("Create At");
        headerRow.createCell(13).setCellValue("Is Public");

        // Lấy danh sách voucher
        List<Voucher> vouchers = voucherService.findAll();

        // Thêm dữ liệu vào sheet
        int rowNum = 1;
        for (Voucher voucher : vouchers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(voucher.getVoucherId());
            row.createCell(1).setCellValue(voucher.getVoucherCode());
            row.createCell(2).setCellValue(voucher.getVoucherName());
            row.createCell(3).setCellValue(voucher.getDescription());
            row.createCell(4).setCellValue(voucher.getDiscountAmount().doubleValue());
            row.createCell(5).setCellValue(voucher.getMinTotalAmount().doubleValue());
            row.createCell(6)
                    .setCellValue(voucher.getMaxTotalAmount() != null ? voucher.getMaxTotalAmount().doubleValue() : 0);
            row.createCell(7).setCellValue(voucher.getMaxUses());
            row.createCell(8).setCellValue(voucher.getMaxUsesUser());
            row.createCell(9).setCellValue(voucher.getType());
            row.createCell(10).setCellValue(voucher.getStartDate() != null ? voucher.getStartDate().toString() : "");
            row.createCell(11).setCellValue(voucher.getEndDate() != null ? voucher.getEndDate().toString() : "");
            row.createCell(12).setCellValue(voucher.getCreateAt().toString());
            row.createCell(13).setCellValue(voucher.getIsPublic() != null ? voucher.getIsPublic() : false);
        }

        // Xuất ra byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        byte[] bytes = outputStream.toByteArray();

        // Đặt tiêu đề cho response
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=vouchers.xlsx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

}
