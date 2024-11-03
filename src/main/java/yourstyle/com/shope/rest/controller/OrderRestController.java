package yourstyle.com.shope.rest.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.service.AddressService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.validation.admin.AddressDto;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/orders")
public class OrderRestController {
    @Autowired
    OrderService orderService;
    @Autowired
    AddressService addressService;
    @Autowired
    CustomerService customerService;

    @PutMapping("update-status")
    public ResponseEntity<String> updateStatusOrder(@RequestBody Map<String, Integer> data, ModelMap model) {
        Integer orderId = data.get("orderId");
        Integer newStatus = data.get("status");
        if (orderId != null && newStatus != null) {
            Optional<Order> orderOptional = orderService.findById(orderId);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                OrderStatus orderStatus = OrderStatus.fromCode(newStatus);
                order.setStatus(orderStatus);
                orderService.save(order);

                return ResponseEntity.ok("Cập nhật trạng thái đơn hàng thành công");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy đơn hàng!");
            }
        }
        return ResponseEntity.badRequest().body("Dữ liệu không hợp lệ");
    }

    // xác nhận lưu địa chỉ
    @PutMapping("submitAddress")
    public ResponseEntity<String> setAddressDefault(@RequestBody AddressDto addressDto) {
        if (addressDto != null) {
            Customer customer = customerService.findById(addressDto.getCustomerId()).get();
            List<Address> listAddresses = addressService.findByAddressCustomerID(customer.getCustomerId());
            Address addressUpdate = addressService.findById(addressDto.getAddressId()).get();
            for (Address address : listAddresses) {
                address.setIsDefault(false);
                addressService.save(address);
            }
            addressUpdate.setIsDefault(true);
            addressService.save(addressUpdate);
            // Trả về phản hồi thành công
            return ResponseEntity.ok("Cập nhật địa chỉ thành công.");
        }
        return ResponseEntity.badRequest().body("Có lỗi xảy ra khi cập nhật địa chỉ!");
    }

    // thêm địa chỉ mới trong modal
    @PostMapping("addAddress")
    public ResponseEntity<List<Address>> addAddress(@RequestBody AddressDto addressDto, Model model) {
        try {
            Optional<Customer> customerOptional = customerService.findById(addressDto.getCustomerId());

            Customer customer = customerOptional.get();
            System.out.println("customer: " + customer);
            Address address = new Address();
            address.setCustomer(customer);
            address.setStreet(addressDto.getStreet());
            address.setWard(addressDto.getWard());
            address.setDistrict(addressDto.getDistrict());
            address.setCity(addressDto.getCity());
            address.setIsDefault(false);
            addressService.save(address);

            List<Address> addresses = addressService.findByAddressCustomerID(customer.getCustomerId());
            return ResponseEntity.ok(addresses);
            // In dữ liệu JSON ra console
            // System.out.println("Response JSON: " + response);
            // return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // In ra thông báo lỗi chi tiết
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

}
