package yourstyle.com.shope.controller.site.carts;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.Voucher;
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.AddressService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.VoucherService;
import yourstyle.com.shope.service.impl.VoucherServiceImpl;

@Controller
@RequestMapping("/yourstyle/carts")
public class OrderDetailController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AddressService addressService;
    @Autowired
    OrderService orderService;
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }

    @GetMapping("/orderdetail")
    public String showCartDetail(Model model) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Integer accountId = customUserDetails.getAccountId();

        Optional<Account> optionalAccount = accountService.findById(accountId);
        Account account = optionalAccount.get();
        Customer customer = customerRepository.findCustomerWithAddresses(account.getCustomer().getCustomerId())
                .orElse(null);
        List<Address> addresses = customer != null ? customer.getAddresses() : new ArrayList<>();

        model.addAttribute("account", account);
        model.addAttribute("customer", customer != null ? customer : new Customer());
        model.addAttribute("addresses", addresses.isEmpty() ? List.of(new Address()) : addresses);

        Address defaultAddress = addresses.stream()
                .filter(Address::getIsDefault)
                .findFirst()
                .orElse(null);
        model.addAttribute("defaultAddress", defaultAddress);

        List<Order> orders = orderService.findByCustomerAndStatus(customer, 9);
        if (orders.isEmpty()) {
            model.addAttribute("errorMessage", "Không có hóa đơn nào đang xử lý.");
            return "errorPage";
        }

        Order currentOrder = orders.get(0);
        BigDecimal totalAmount = currentOrder.getTotalAmount();
        model.addAttribute("orderTotal", totalAmount);
        model.addAttribute("orderDate", currentOrder.getOrderDate());

        // Lọc voucher dựa trên tổng tiền
        List<Voucher> vouchers = voucherService.findVouchersByTotalAmount(totalAmount);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        // Định dạng ngày bắt đầu và kết thúc của voucher
        List<Voucher> validVouchers = vouchers.stream()
                .filter(voucher -> {
                    LocalDateTime now = LocalDateTime.now();
                    boolean isValid = (voucher.getStartDate() == null || !voucher.getStartDate().isAfter(now)) &&
                            (voucher.getEndDate() == null || !voucher.getEndDate().isBefore(now));
                    return isValid;
                })
                .collect(Collectors.toList());

        // Định dạng ngày cho các voucher
        validVouchers.forEach(voucher -> {
            if (voucher.getStartDate() != null) {
                String formattedStartDate = voucher.getStartDate().format(formatter);
                model.addAttribute("formattedStartDate", formattedStartDate);
            }
            if (voucher.getEndDate() != null) {
                String formattedEndDate = voucher.getEndDate().format(formatter);
                model.addAttribute("formattedEndDate", formattedEndDate);
            }
        });

        model.addAttribute("vouchers", validVouchers);
        model.addAttribute("isCheckoutPage", true);
        return "site/carts/orderdetail";
    }

    @PostMapping("/save")
    public String saveCustomerAndAddress(
            @ModelAttribute("customer") Customer customer,
            @RequestParam(value = "defaultAddress.street", required = false) String street,
            @RequestParam(value = "defaultAddress.ward", required = false) String ward,
            @RequestParam(value = "defaultAddress.district", required = false) String district,
            @RequestParam(value = "defaultAddress.city", required = false) String city,
            @RequestParam(value = "isDefault", defaultValue = "true") boolean isDefault) {

        if (customer.getAccount() == null || customer.getAccount().getAccountId() == null) {
            throw new IllegalStateException("Account information is missing.");
        }

        Customer existingCustomer = customerService.findByAccountId(customer.getAccount().getAccountId());

        if (existingCustomer == null) {
            customer.setAccount(customer.getAccount());
            existingCustomer = customerService.save(customer);
        } else {
            existingCustomer.setFullname(customer.getFullname());
            existingCustomer.setPhoneNumber(customer.getPhoneNumber());
            customerService.save(existingCustomer);
        }

        if (street != null && ward != null && district != null && city != null) {
            Address newAddress = new Address();
            newAddress.setStreet(street);
            newAddress.setWard(ward);
            newAddress.setDistrict(district);
            newAddress.setCity(city);
            newAddress.setIsDefault(isDefault);
            newAddress.setCustomer(existingCustomer);

            addressService.save(newAddress);
        }

        return "redirect:/yourstyle/carts/orderdetail";
    }

    @PostMapping("/addAddress")
    public String addAddress(
            @RequestParam(value = "defaultAddress.street") String street,
            @RequestParam(value = "defaultAddress.ward") String ward,
            @RequestParam(value = "defaultAddress.district") String district,
            @RequestParam(value = "defaultAddress.city") String city,
            @RequestParam(value = "isDefault", defaultValue = "false") boolean isDefault) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Integer accountId = customUserDetails.getAccountId();

        Customer existingCustomer = customerService.findByAccountId(accountId);

        if (existingCustomer != null) {
            saveAddress(existingCustomer, street, ward, district, city, isDefault);
        }

        return "redirect:/yourstyle/carts/orderdetail";
    }

    private void saveAddress(Customer existingCustomer, String street, String ward, String district, String city,
            boolean isDefault) {
        Address newAddress = new Address();
        newAddress.setStreet(street);
        newAddress.setWard(ward);
        newAddress.setDistrict(district);
        newAddress.setCity(city);
        newAddress.setIsDefault(isDefault);
        newAddress.setCustomer(existingCustomer);

        addressService.save(newAddress);
    }

    @GetMapping("/orderDetail/deleteAddress/{addressId}")
    public String deleteAddress(@PathVariable("addressId") Integer addressId,
            @RequestParam(value = "customerId", required = false) String customerIdStr,
            RedirectAttributes redirectAttributes) {

        if (addressId != null) {
            Optional<Address> address = addressService.findById(addressId);

            if (address.isPresent()) {
                addressService.deleteById(addressId);
                redirectAttributes.addFlashAttribute("messageType", "success");
                redirectAttributes.addFlashAttribute("messageContent", "Xóa địa chỉ thành công");
            } else {
                redirectAttributes.addFlashAttribute("messageType", "error");
                redirectAttributes.addFlashAttribute("messageContent", "Địa chỉ không tồn tại");
            }
        } else {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("messageContent", "Địa chỉ không hợp lệ");
        }

        return "redirect:/yourstyle/carts/orderdetail";
    }

    @GetMapping("/setDefaultAddress/{addressId}")
    public String setDefaultAddress(@PathVariable("addressId") Integer addressId,
            RedirectAttributes redirectAttributes) {
        Optional<Address> selectedAddressOpt = addressService.findById(addressId);

        if (selectedAddressOpt.isPresent()) {
            Address selectedAddress = selectedAddressOpt.get();
            Customer customer = selectedAddress.getCustomer();

            customer.getAddresses().forEach(address -> {
                if (address.getIsDefault()) {
                    address.setIsDefault(false);
                    addressService.save(address);
                }
            });

            selectedAddress.setIsDefault(true);
            addressService.save(selectedAddress);

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("messageContent", "Đã đặt địa chỉ mặc định thành công.");
        } else {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("messageContent", "Địa chỉ không tồn tại.");
        }

        return "redirect:/yourstyle/carts/orderdetail";
    }

}
