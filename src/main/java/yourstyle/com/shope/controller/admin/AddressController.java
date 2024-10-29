package yourstyle.com.shope.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.service.AddressService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.validation.admin.AddressDTO;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("admin/addresses")
public class AddressController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @GetMapping("listAddress/{customerId}")
    public ModelAndView editAddressCustomer(ModelMap model, @PathVariable("customerId") Integer customerId) {

        // Lấy danh sách địa chỉ của khách hàng
        List<Address> addresses = addressService.findByCustomerId(customerId);
        model.addAttribute("address", new AddressDTO());
        model.addAttribute("addresses", addresses);
        model.addAttribute("customerId", customerId);
        return new ModelAndView("admin/customers/listAddress", model);
    }

    @GetMapping("edit/{addressId}")
    public ModelAndView editAddress(@PathVariable("addressId") Integer addressId,
            @RequestParam("customerId") Integer customerId,
            ModelMap model) {

        // Xóa địa chỉ dựa trên addressId
        Address address = addressService.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid address ID"));

        // Lấy lại danh sách địa chỉ sau khi xóa
        List<Address> addresses = addressService.findByCustomerId(customerId);
        model.addAttribute("addresses", addresses);
        model.addAttribute("address", address); // Địa chỉ để chỉnh sửa
        model.addAttribute("customerId", customerId);

        return new ModelAndView("admin/customers/listAddress", model);
    }

    @PostMapping("saveOrUpdate")
    public ModelAndView saveOrUpdate(ModelMap model,
            @Valid @ModelAttribute AddressDTO addressDTO,
            BindingResult result,
            @RequestParam("customerId") Integer customerId) {

        // Kiểm tra lỗi đầu vào
        if (result.hasErrors()) {
            model.addAttribute("address", addressDTO);
            model.addAttribute("messageType", "error");
            model.addAttribute("messageContent", "Lỗi Kiểm tra lại thông tin!");

            // Lấy lại danh sách địa chỉ của khách hàng để cập nhật trên trang
            List<Address> addresses = addressService.findByCustomerId(customerId);
            model.addAttribute("addresses", addresses);
            model.addAttribute("customerId", customerId);
            return new ModelAndView("admin/customers/listAddress", model);
        }

        // Tìm đối tượng Customer dựa trên customerId
        Customer customer = customerService.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));

        // Tạo hoặc cập nhật địa chỉ
        Address address;
        if (addressDTO.getAddressId() != null) {
            // Nếu địa chỉ đã tồn tại, tìm địa chỉ để cập nhật
            address = addressService.findById(addressDTO.getAddressId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid address ID"));
            address.setStreet(addressDTO.getStreet());
            address.setCity(addressDTO.getCity());
            address.setDistrict(addressDTO.getDistrict());
            address.setWard(addressDTO.getWard());
            address.setIsDefault(addressDTO.getIsDefault());
        } else {
            // Nếu địa chỉ chưa tồn tại, tạo mới
            address = new Address();
            address.setCustomer(customer);
            address.setStreet(addressDTO.getStreet());
            address.setCity(addressDTO.getCity());
            address.setDistrict(addressDTO.getDistrict());
            address.setWard(addressDTO.getWard());
            address.setIsDefault(addressDTO.getIsDefault());
        }

        // Lưu địa chỉ
        addressService.save(address);

        // Lấy lại danh sách địa chỉ của khách hàng để cập nhật trên trang
        List<Address> addresses = addressService.findByCustomerId(customerId);
        model.addAttribute("address", new AddressDTO()); // Đặt lại form trống
        model.addAttribute("addresses", addresses);
        model.addAttribute("customerId", customerId);

        return new ModelAndView("admin/customers/listAddress", model);
    }

    @GetMapping("delete/{addressId}")
    public ModelAndView deleteAddress(@PathVariable("addressId") Integer addressId,
            @RequestParam("customerId") Integer customerId,
            ModelMap model) {

        // Xóa địa chỉ dựa trên addressId
        addressService.deleteById(addressId);

        // Lấy lại danh sách địa chỉ sau khi xóa
        List<Address> addresses = addressService.findByCustomerId(customerId);
        model.addAttribute("addresses", addresses);
        model.addAttribute("address", new Address()); // Đặt lại form trống
        model.addAttribute("customerId", customerId);

        return new ModelAndView("admin/customers/listAddress", model);
    }

    @GetMapping("setDefault/{addressId}")
    public ModelAndView setDefaultAddress(@PathVariable("addressId") Integer addressId,
            @RequestParam("customerId") Integer customerId,
            ModelMap model) {
        // Bỏ dấu mặc định của địa chỉ hiện tại (nếu có)
        addressService.removeDefaultAddress(customerId);

        // Đặt địa chỉ được chọn làm mặc định
        Address address = addressService.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid address ID"));
        address.setIsDefault(true);
        addressService.save(address);

        List<Address> addresses = addressService.findByCustomerId(customerId);
        model.addAttribute("addresses", addresses);
        model.addAttribute("address", new Address());
        model.addAttribute("customerId", customerId);
        return new ModelAndView("admin/customers/listAddress", model);
    }

}
