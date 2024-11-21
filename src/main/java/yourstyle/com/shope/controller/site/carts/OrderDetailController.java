package yourstyle.com.shope.controller.site.carts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    private VoucherServiceImpl voucherServiceImpl;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;

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

        model.addAttribute("vouchers", vouchers);

        return "site/carts/orderdetail";
    }

    @PostMapping("/save")
    public String saveCustomerAndAddress(@ModelAttribute("customer") Customer customer,
            @RequestParam("defaultAddress.street") String street,
            @RequestParam("defaultAddress.ward") String ward,
            @RequestParam("defaultAddress.district") String district,
            @RequestParam("defaultAddress.city") String city,
            @RequestParam(value = "isDefault", defaultValue = "true") boolean isDefault) {

        if (customer.getAccount() == null || customer.getAccount().getAccountId() == null) {
            throw new IllegalStateException("Account information is missing.");
        }

        Customer existingCustomer = customerService.findByAccountId(customer.getAccount().getAccountId());
        if (existingCustomer == null) {
            customer.setAccount(customer.getAccount());
            existingCustomer = customerService.save(customer);
        }

        existingCustomer.setFullname(customer.getFullname());
        existingCustomer.setPhoneNumber(customer.getPhoneNumber());
        customerService.save(existingCustomer);

        // Kiểm tra nếu danh sách địa chỉ không phải là null
        if (existingCustomer.getAddresses() == null) {
            existingCustomer.setAddresses(new ArrayList<>());
        }

        Address defaultAddress = customer.getDefaultAddress();
        if (defaultAddress == null) {
            throw new IllegalStateException("No default address found for the customer.");
        }

        Address existingAddress = existingCustomer.getAddresses().isEmpty() ? null
                : existingCustomer.getAddresses().get(0);

        if (existingAddress != null) {
            existingAddress.setStreet(street);
            existingAddress.setWard(ward);
            existingAddress.setDistrict(district);
            existingAddress.setCity(city);
            existingAddress.setIsDefault(isDefault);
            addressService.save(existingAddress);
        } else {
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
    public String addNewAddress(@ModelAttribute("customer") Customer customer,
            @RequestParam("newStreet") String street,
            @RequestParam("newWard") String ward,
            @RequestParam("newDistrict") String district,
            @RequestParam("newCity") String city,
            @RequestParam(value = "newIsDefault", defaultValue = "false") boolean isDefault,
            RedirectAttributes redirectAttributes) {

        if (customer.getAccount() == null || customer.getAccount().getAccountId() == null) {
            throw new IllegalStateException("Account information is missing.");
        }

        Customer existingCustomer = customerService.findByAccountId(customer.getAccount().getAccountId());

        if (existingCustomer == null) {
            customer.setAccount(customer.getAccount());
            existingCustomer = customerService.save(customer);
        }

        // Kiểm tra nếu người dùng đã có 3 địa chỉ
        if (existingCustomer.getAddresses().size() >= 3) {
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addFlashAttribute("messageContent", "Người dùng chỉ có thể có tối đa 3 địa chỉ.");
            return "redirect:/yourstyle/carts/orderdetail";
        }

        Address newAddress = new Address();
        newAddress.setStreet(street);
        newAddress.setWard(ward);
        newAddress.setDistrict(district);
        newAddress.setCity(city);
        newAddress.setIsDefault(isDefault);
        newAddress.setCustomer(existingCustomer);

        if (isDefault) {
            existingCustomer.getAddresses().forEach(address -> address.setIsDefault(false));
        }

        addressService.save(newAddress);

        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("messageContent", "Thêm địa chỉ thành công.");
        return "redirect:/yourstyle/carts/orderdetail";
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
