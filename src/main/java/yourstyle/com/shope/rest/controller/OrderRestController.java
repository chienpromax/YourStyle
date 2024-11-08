package yourstyle.com.shope.rest.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import yourstyle.com.shope.model.Address;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Order;
import yourstyle.com.shope.model.OrderStatus;
import yourstyle.com.shope.model.OrderStatusHistory;
import yourstyle.com.shope.service.AddressService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.OrderStatusHistoryService;
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
    @Autowired
    OrderStatusHistoryService orderStatusHistoryService;

    @PutMapping("update-status")
    public ResponseEntity<Map<String, Object>> updateStatusOrder(@RequestBody Map<String, Integer> data,
            ModelMap model) {
        Integer orderId = data.get("orderId");
        Integer newStatus = data.get("status");
        Map<String, Object> response = new HashMap<>();
        if (orderId != null && newStatus != null) {
            Optional<Order> orderOptional = orderService.findById(orderId);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                OrderStatus orderStatus = OrderStatus.fromCode(newStatus);

                OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
                orderStatusHistory.setOrder(order);
                orderStatusHistory.setStatus(orderStatus.getDescription());
                orderStatusHistory.setStatusTime(new Timestamp(System.currentTimeMillis()));
                orderStatusHistoryService.save(orderStatusHistory);

                order.setStatus(orderStatus);
                orderService.save(order);
                // switch (newStatus) {
                // case 0:
                // order.setCanceledAt(new Timestamp(System.currentTimeMillis()));
                // break;
                // case 2:
                // order.setPackedAt(new Timestamp(System.currentTimeMillis()));
                // break;
                // case 3:
                // order.setShippedAt(new Timestamp(System.currentTimeMillis()));
                // break;
                // case 4:
                // order.setInTransitAt(new Timestamp(System.currentTimeMillis()));
                // break;
                // case 5:
                // order.setCompletedAt(new Timestamp(System.currentTimeMillis()));
                // break;
                // case 6:
                // order.setReturnedAt(new Timestamp(System.currentTimeMillis()));
                // break;
                // default:
                // break;
                // }

                response.put("orderId", orderId);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Không tìm thấy đơn hàng!"));
            }
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu không hợp lệ"));
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
    public ResponseEntity<List<AddressDto>> addAddress(@RequestBody AddressDto addressDto) {
        try {
            Optional<Customer> customerOptional = customerService.findById(addressDto.getCustomerId());

            if (!customerOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            System.out.println("Length of city: " + addressDto.getCity().length());

            Customer customer = customerOptional.get();
            Address address = new Address();
            address.setCustomer(customer);
            address.setStreet(addressDto.getStreet().trim());
            address.setWard(addressDto.getWard().trim());
            address.setDistrict(addressDto.getDistrict().trim());
            address.setCity(addressDto.getCity().trim());
            address.setIsDefault(false);
            addressService.save(address);

            List<AddressDto> addressesDtos = addressService.findByAddressDtoCustomerID(customer.getCustomerId());
            return ResponseEntity.ok(addressesDtos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
