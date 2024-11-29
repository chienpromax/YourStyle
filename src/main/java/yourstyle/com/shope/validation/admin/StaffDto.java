package yourstyle.com.shope.validation.admin;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffDto {
      private Integer customerId;
    private String fullname;
    private String email;
    private String phoneNumber;
    private String role;
    private Boolean status;
    private String avatar;
    private Boolean gender;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    

   
    

    
    
}