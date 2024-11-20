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
import yourstyle.com.shope.model.TransactionType;
import yourstyle.com.shope.service.AddressService;
import yourstyle.com.shope.service.CustomerService;
import yourstyle.com.shope.service.OrderService;
import yourstyle.com.shope.service.OrderStatusHistoryService;
import yourstyle.com.shope.validation.admin.AddressDto;
import yourstyle.com.shope.validation.admin.OrderDto;
import yourstyle.com.shope.validation.admin.OrderStatusHistoryDto;

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

    @SuppressWarnings("static-access")
    @PutMapping("update-status")
    public ResponseEntity<OrderStatusHistoryDto> updateStatusOrder(@RequestBody Map<String, Integer> data,
            ModelMap model) {
        Integer orderId = data.get("orderId");
        Integer newStatus = data.get("status");
        if (orderId != null && newStatus != null) {
            Optional<Order> orderOptional = orderService.findById(orderId);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                OrderStatus orderStatus = OrderStatus.fromCode(newStatus);

                OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
                orderStatusHistory.setOrder(order);
                int typeStatus = orderStatus.getCode();
                switch (typeStatus) {
                    case 0:
                        orderStatusHistory.setStatus(String.valueOf(orderStatus.CANCELED));
                        break;
                    case 1:
                        orderStatusHistory.setStatus(String.valueOf(orderStatus.PLACED));
                        break;
                    case 2:
                        orderStatusHistory.setStatus(String.valueOf(orderStatus.PACKING));
                        break;
                    case 3:
                        orderStatusHistory.setStatus(String.valueOf(orderStatus.SHIPPED));
                        break;
                    case 4:
                        orderStatusHistory.setStatus(String.valueOf(orderStatus.IN_TRANSIT));
                        break;
                    case 5:
                        orderStatusHistory.setStatus(String.valueOf(orderStatus.PAID));
                        break;
                    case 6:
                        orderStatusHistory.setStatus(String.valueOf(orderStatus.COMPLETED));
                        break;
                    case 8:
                        orderStatusHistory.setStatus(String.valueOf(orderStatus.RETURNED));
                        break;
                    case 9:
                        orderStatusHistory.setStatus(String.valueOf(orderStatus.BUYING));
                        break;
                    default:
                        break;
                }
                orderStatusHistory.setStatusTime(new Timestamp(System.currentTimeMillis()));
                orderStatusHistoryService.save(orderStatusHistory);

                order.setStatus(orderStatus);
                orderService.save(order);
                OrderStatusHistory orderStatusHistoryRepository = orderStatusHistoryService.findByLatestStatus(
                        order.getOrderId(),
                        orderStatusHistory.getStatus()).get();
                OrderStatusHistoryDto orderStatusHistoryDto = new OrderStatusHistoryDto(
                        orderStatusHistoryRepository.getStatusTime(),
                        orderStatusHistoryRepository.getStatus(),
                        order.getStatusDescription());
                return ResponseEntity.ok(orderStatusHistoryDto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }
        return ResponseEntity.badRequest().body(null);
    }

    // xác nhận lưu địa chỉ
    @PutMapping("submitAddress")
    public ResponseEntity<AddressDto> setAddressDefault(@RequestBody AddressDto addressDto) {
        if (addressDto != null) {
            Customer customer = customerService.findById(addressDto.getCustomerId()).get();
            // kiểm tra xem có thay đổi tên và sdt không nếu có thì cập nhật
            customer.setFullname(addressDto.getCustomer().getFullname());
            customer.setPhoneNumber(addressDto.getCustomer().getPhoneNumber());
            customerService.save(customer);

            // Danh sách địa chỉ của khách hàng
            List<Address> listAddresses = addressService.findByAddressCustomerID(customer.getCustomerId());
            Address addressUpdate = addressService.findById(addressDto.getAddressId()).get();
            for (Address address : listAddresses) {
                address.setIsDefault(false);
                addressService.save(address);
            }
            addressUpdate.setIsDefault(true);
            addressService.save(addressUpdate);
            // Trả về phản hồi thành công
            AddressDto addressDtoReponse = new AddressDto(
                    addressUpdate.getCustomer().getFullname(),
                    addressUpdate.getCustomer().getPhoneNumber(),
                    addressUpdate.getStreet(),
                    addressUpdate.getWard(),
                    addressUpdate.getDistrict(),
                    addressUpdate.getCity());
            return ResponseEntity.ok(addressDtoReponse);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    // thêm địa chỉ mới trong modal
    @PostMapping("addAddress")
    public ResponseEntity<List<AddressDto>> addAddress(@RequestBody AddressDto addressDto) {
        try {
            Optional<Customer> customerOptional = customerService.findById(addressDto.getCustomerId());
            if (!customerOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            Customer customer = customerOptional.get();
            // kiểm tra địa chỉ vượt quá 3 thì không cho thêm
            if (customer.getAddresses().size() >= 3) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
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

    // hàm thanh toán
    @SuppressWarnings("unlikely-arg-type")
    @PutMapping("payment")
    public ResponseEntity<OrderDto> payment(@RequestBody OrderDto orderDto) {
        System.out.println("Received OrderDto: " + orderDto);
        Optional<Order> orderOptional = orderService.findById(orderDto.getOrderId());
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if ("COD".equals(orderDto.getTransactionType())) {
                order.setTransactionType(TransactionType.COD);
                order.setPaymentMethod("Tiền mặt");
            } else {
                order.setTransactionType(TransactionType.BANK_TRANSFER);
                order.setPaymentMethod("Chuyển khoản");
            }
            order.setTransactionStatus("Thành công");
            order.setTransactionTime(new Timestamp(System.currentTimeMillis()));
            orderService.save(order);
            OrderDto orderDtoResponse = new OrderDto(
                    order.getTotalAmount(),
                    order.getTransactionType().toString(),
                    order.getTransactionTime(),
                    order.getPaymentMethod(),
                    order.getTransactionStatus());
            return ResponseEntity.ok(orderDtoResponse);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
