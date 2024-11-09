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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            @RequestParam(value = "isDefault", defaultValue = "false") boolean isDefault) {

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

        Address address = new Address();
        address.setStreet(street);
        address.setWard(ward);
        address.setDistrict(district);
        address.setCity(city);
        address.setIsDefault(isDefault);
        address.setCustomer(existingCustomer);
        addressService.save(address);

        return "redirect:/yourstyle/carts/orderdetail";
    }

}
