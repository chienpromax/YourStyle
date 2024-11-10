package yourstyle.com.shope.controller.site.carts;

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
import yourstyle.com.shope.repository.CustomerRepository;
import yourstyle.com.shope.service.AccountService;
import yourstyle.com.shope.service.AddressService;
import yourstyle.com.shope.service.CustomerService;

@Controller
@RequestMapping("/yourstyle/carts")
public class OrderDetailController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AddressService addressService;
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

        return "site/carts/orderdetail";
    }

    @PostMapping("/save")
    public String saveCustomerAndAddress(@ModelAttribute("customer") Customer customer,
            @RequestParam("addresses[0].street") String street,
            @RequestParam("addresses[0].ward") String ward,
            @RequestParam("addresses[0].district") String district,
            @RequestParam("addresses[0].city") String city,
            @RequestParam(value = "isDefault", defaultValue = "true") boolean isDefault) {

        // Kiểm tra và khởi tạo Account nếu cần
        if (customer.getAccount() == null || customer.getAccount().getAccountId() == null) {
            // Xử lý khi `Account` chưa được thiết lập
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

        // Kiểm tra nếu khách hàng đã có địa chỉ, nếu có thì cập nhật địa chỉ đầu tiên
        Address existingAddress = existingCustomer.getAddresses().isEmpty() ? null
                : existingCustomer.getAddresses().get(0);

        if (existingAddress != null) {
            // Cập nhật địa chỉ hiện tại
            existingAddress.setStreet(street);
            existingAddress.setWard(ward);
            existingAddress.setDistrict(district);
            existingAddress.setCity(city);
            existingAddress.setIsDefault(isDefault);
            addressService.save(existingAddress);
        } else {
            // Nếu không có địa chỉ, tạo mới địa chỉ
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
            @RequestParam(value = "newIsDefault", defaultValue = "false") boolean isDefault) {

        // Kiểm tra và khởi tạo Account nếu cần
        if (customer.getAccount() == null || customer.getAccount().getAccountId() == null) {
            throw new IllegalStateException("Account information is missing.");
        }

        // Lấy thông tin khách hàng từ cơ sở dữ liệu
        Customer existingCustomer = customerService.findByAccountId(customer.getAccount().getAccountId());

        if (existingCustomer == null) {
            customer.setAccount(customer.getAccount());
            existingCustomer = customerService.save(customer);
        }

        // Tạo mới địa chỉ và lưu vào cơ sở dữ liệu
        Address newAddress = new Address();
        newAddress.setStreet(street);
        newAddress.setWard(ward);
        newAddress.setDistrict(district);
        newAddress.setCity(city);
        newAddress.setIsDefault(isDefault);
        newAddress.setCustomer(existingCustomer);
        addressService.save(newAddress);

        // Chuyển hướng lại trang để cập nhật danh sách địa chỉ
        return "redirect:/yourstyle/carts/orderdetail";
    }

    @GetMapping("/orderDetail/deleteAddress/{addressId}")
    public String deleteAddress(@PathVariable("addressId") Integer addressId,
            @RequestParam(value = "customerId", required = false) String customerIdStr,
            RedirectAttributes redirectAttributes) {
    
        Integer customerId = null;
    
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
            @RequestParam("customerId") Integer customerId,
            RedirectAttributes redirectAttributes) {
        // Bỏ dấu mặc định của địa chỉ hiện tại (nếu có)
        addressService.removeDefaultAddress(customerId);

        // Đặt địa chỉ được chọn làm mặc định
        Address address = addressService.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid address ID"));
        address.setIsDefault(true);
        addressService.save(address);

        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("messageContent", "Đặt làm địa chỉ mặc định thành công");

        // Sau khi đặt làm mặc định, trả về danh sách địa chỉ mới
        return "redirect:/orderDetail/addresses?customerId=" + customerId;
    }

}
