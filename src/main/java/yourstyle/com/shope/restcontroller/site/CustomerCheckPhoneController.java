package yourstyle.com.shope.restcontroller.site;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import yourstyle.com.shope.repository.CustomerRepository;

@RestController
@RequestMapping("/api/customers")
public class CustomerCheckPhoneController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/check-phone")
    public ResponseEntity<Map<String, Boolean>> checkPhone(
        @RequestParam String phoneNumber,
        @RequestParam(required = false) Integer customerId) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }

        boolean exists;
        if (customerId == null) {
            // Trường hợp thêm mới: chỉ kiểm tra số điện thoại đã tồn tại
            exists = customerRepository.existsByPhoneNumber(phoneNumber);
        } else {
            // Trường hợp cập nhật: kiểm tra số điện thoại khác với customerId hiện tại
            exists = customerRepository.existsByPhoneNumberAndCustomerIdNot(phoneNumber, customerId);
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}

