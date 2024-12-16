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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Voucher;
import yourstyle.com.shope.model.VoucherCustomer;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.EmailService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.VoucherCustomerService;
import yourstyle.com.shope.service.VoucherService;
import yourstyle.com.shope.validation.admin.VoucherDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
@RequestMapping("admin/vouchers")
public class VoucherController {
    @Autowired
    VoucherService voucherService;

    @Autowired
    CustomerService customerService;

    @Autowired
    AccountService accountService;

    @Autowired
    VoucherCustomerService voucherCustomerService;

    @Autowired
    EmailService emailService;

    @Autowired
    OrderService orderService;

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
            voucher.setMinTotalAmount(
                    voucher.getMinTotalAmount().setScale(0, RoundingMode.FLOOR).multiply(new BigDecimal(1000)));
            voucher.setMaxTotalAmount(
                    voucher.getMaxTotalAmount().setScale(0, RoundingMode.FLOOR).multiply(new BigDecimal(1000)));
            if (voucher.getType() == 2) {
                voucher.setDiscountAmount(
                        voucher.getDiscountAmount().setScale(0, RoundingMode.FLOOR));
            } else {
                voucher.setDiscountAmount(
                        voucher.getDiscountAmount().setScale(0, RoundingMode.FLOOR).multiply(new BigDecimal(1000)));
            }

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
    public ModelAndView saveOrUpdate(
            @Valid @ModelAttribute("voucher") VoucherDTO voucherDto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            ModelMap model) {

        // Lấy thông tin người dùng hiện tại
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountService.findByUsername(username);
        voucherDto.setAccount(account); // Thiết lập người tạo

        validationVoucher(voucherDto, result);

        if (result.hasErrors()) {
            // Nếu có lỗi, lấy danh sách khách hàng và trả về form
            List<Customer> customers = customerService.findAll();
            model.addAttribute("customers", customers);
            model.addAttribute("voucher", voucherDto);
            model.addAttribute("messageType", "error");
            model.addAttribute("messageContent", "Lỗi! Vui lòng kiểm tra thông tin!");
            return new ModelAndView("admin/vouchers/addOrEdit", model);
        }
        voucherDto.setMinTotalAmount(roundAndRemoveTrailingZeros(voucherDto.getMinTotalAmount()));
        voucherDto.setMaxTotalAmount(roundAndRemoveTrailingZeros(voucherDto.getMaxTotalAmount()));
        if (voucherDto.getType() != 2) {
            voucherDto.setDiscountAmount(roundAndRemoveTrailingZeros(voucherDto.getDiscountAmount()));
        }

        // Tạo hoặc cập nhật voucher
        Voucher voucher = new Voucher();
        if (voucherDto.getVoucherId() != null) {
            // Nếu voucherId tồn tại, kiểm tra và cập nhật
            Optional<Voucher> existingVoucherOpt = voucherService.findByVoucherId(voucherDto.getVoucherId());
            if (existingVoucherOpt.isPresent()) {
                Voucher existingVoucher = existingVoucherOpt.get();
                BeanUtils.copyProperties(voucherDto, existingVoucher, "voucherId");
                voucherService.save(existingVoucher); // Cập nhật voucher
                redirectAttributes.addFlashAttribute("messageType", "success");
                redirectAttributes.addFlashAttribute("messageContent", "Cập nhật voucher thành công!");
            } else {
                redirectAttributes.addFlashAttribute("messageType", "error");
                redirectAttributes.addFlashAttribute("messageContent", "Voucher không tồn tại.");
                return new ModelAndView("admin/vouchers/addOrEdit", model);
            }
        } else {
            // Nếu voucherId không tồn tại, thêm mới
            BeanUtils.copyProperties(voucherDto, voucher);
            voucherService.save(voucher);
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("messageContent", "Thêm voucher thành công!");
        }

        // Gửi email tới tất cả khách hàng với HTML
        List<Customer> customers = customerService.findAll();
        String subject = "🎉 Voucher mới từ YourStyle đến! 🎉";
        String htmlBodyTemplate = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <style>
                            body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f7f7f7; }
                            .email-container { max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); overflow: hidden; }
                            .email-header { background-color: #4CAF50; color: white; text-align: center; padding: 20px 0; }
                            .email-content { padding: 20px; color: #333; }
                            .email-footer { background-color: #f1f1f1; text-align: center; padding: 10px; font-size: 12px; color: #777; }
                            .cta-button { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }
                            .cta-button:hover { background-color: #45a049; }
                        </style>
                    </head>
                    <body>
                        <div class="email-container">
                            <div class="email-header">
                                <h1>🎉 Voucher Mới Từ YourStyle! 🎉</h1>
                            </div>
                            <div class="email-content">
                                <p>Chào bạn,</p>
                                <p>Chúng tôi rất vui mừng thông báo rằng bạn đã nhận được một voucher giảm giá hấp dẫn:</p>
                                <ul>
                                    <li><strong>Tên giảm giá:</strong> %s</li>
                                    <li><strong>Mã giảm giá:</strong> %s</li>
                                    <li><strong>Giảm giá:</strong> %s%%</li>
                                    <li><strong>Hạn sử dụng:</strong> %s</li>
                                </ul>
                                <p>Hãy sử dụng voucher này ngay hôm nay để tận hưởng ưu đãi đặc biệt của chúng tôi!</p>
                                <a href="http://localhost:8080/yourstyle/home" class="cta-button">Sử Dụng Ngay</a>
                            </div>
                            <div class="email-footer">
                                <p>Cảm ơn bạn đã đồng hành cùng YourStyle!</p>
                            </div>
                        </div>
                    </body>
                    </html>
                """;

        // for (Customer customer : customers) {
        // if (customer.getAccount() != null && customer.getAccount().getEmail() !=
        // null) {
        // // Chèn dữ liệu vào template HTML
        // String htmlBody = String.format(
        // htmlBodyTemplate,
        // voucher.getVoucherName(),
        // voucher.getVoucherCode(),
        // voucher.getDiscountAmount(),
        // voucher.getEndDate());
        // emailService.sendVoucherEmail(customer.getAccount().getEmail(), subject,
        // htmlBody);
        // }
        // }

        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("messageContent", "Thêm/cập nhật voucher và gửi email thành công!");
        return new ModelAndView("redirect:/admin/vouchers");
    }

    public void validationVoucher(VoucherDTO voucherDto, BindingResult result) {
        if (voucherDto.getVoucherCode() != null && voucherService.existsByVoucherCode(voucherDto.getVoucherCode())) {
            result.rejectValue("voucherCode", "error.voucherCode", "Mã voucher đã tồn tại.");
        }

        if (voucherDto.getVoucherName() != null && voucherService.existsByVoucherName(voucherDto.getVoucherName())) {
            result.rejectValue("voucherName", "error.voucherName", "Tên voucher đã tồn tại.");
        }

        if (voucherDto.getMinTotalAmount() != null && voucherDto.getMaxTotalAmount() != null) {
            if (voucherDto.getMinTotalAmount().compareTo(voucherDto.getMaxTotalAmount()) > 0) {
                result.rejectValue("minTotalAmount", "error.minTotalAmount",
                        "Giá trị nhỏ nhất không được lớn hơn giá trị lớn nhất.");
            }
        }

        if (voucherDto.getDiscountAmount() != null) {
            BigDecimal minAmount = voucherDto.getMinTotalAmount();

            if (minAmount != null && voucherDto.getDiscountAmount().compareTo(minAmount) > 0) {
                result.rejectValue("discountAmount", "error.discountAmount",
                        "Giảm giá không được lớn hơn giá trị tối thiểu.");
            }
        }

        if (voucherDto.getType() == 2) {
            if (voucherDto.getDiscountAmount() != null) {
                BigDecimal minDiscount = new BigDecimal("1");
                BigDecimal maxDiscount = new BigDecimal("100");

                if (voucherDto.getDiscountAmount().compareTo(minDiscount) < 0
                        || voucherDto.getDiscountAmount().compareTo(maxDiscount) > 0) {
                    result.rejectValue("discountAmount", "error.discountAmount",
                            "Giảm giá phần trăm phải từ 1 đến 100.");
                }
            }
        } else {
            if (voucherDto.getDiscountAmount() != null) {
                BigDecimal minAmount = new BigDecimal("1000");
                if (voucherDto.getDiscountAmount().compareTo(minAmount) <= 0) {
                    result.rejectValue("discountAmount", "error.discountAmount",
                            "Số tiền tối thiều phải lớn hơn 1000.");
                }
            }
        }

    }

    public static BigDecimal roundAndRemoveTrailingZeros(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal thousand = new BigDecimal(1000);
        BigDecimal rounded = amount.divide(thousand, 0, RoundingMode.HALF_UP); // Làm tròn đến hàng nghìn
        return rounded.stripTrailingZeros(); // Loại bỏ phần thập phân nếu có
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
    public ModelAndView getMethodName(ModelMap model, @PathVariable("voucherId") Integer voucherId) {

        if (voucherId != null) {
            boolean isUsedInOrders = orderService.existsByVoucherId(voucherId);

            if (isUsedInOrders) {
                model.addAttribute("messageType", "error");
                model.addAttribute("messageContent", "Không thể xóa. Voucher đang được sử dụng trong các đơn hàng.");
            } else {
                // Xóa tất cả các bản ghi liên quan trong VoucherCustomer trước
                voucherCustomerService.deleteByVoucherId(voucherId);
                voucherService.deleteByVoucherId(voucherId);
                model.addAttribute("messageType", "success");
                model.addAttribute("messageContent", "Xóa thành công");
            }
        } else {
            model.addAttribute("messageType", "error");
            model.addAttribute("messageContent", "Khách hàng không tồn tại");
        }
        return new ModelAndView("redirect:/admin/vouchers", model);
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
