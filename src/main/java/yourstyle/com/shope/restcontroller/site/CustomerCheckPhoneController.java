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
        boolean exists = customerRepository.existsByPhoneNumberAndCustomerIdNot(phoneNumber, customerId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

}
