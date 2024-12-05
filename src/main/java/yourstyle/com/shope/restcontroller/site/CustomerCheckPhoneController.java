package yourstyle.com.shope.restcontroller.site;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import yourstyle.com.shope.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerCheckPhoneController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/check-phone")
    public ResponseEntity<?> checkPhoneNumber(@RequestParam String phoneNumber) {
        boolean exists = customerService.existsByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }
}
