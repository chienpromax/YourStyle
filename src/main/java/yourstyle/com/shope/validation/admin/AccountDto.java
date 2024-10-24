package yourstyle.com.shope.validation.admin;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Role;


@Data
public class AccountDto {
	  private Integer accountId;

	  private String email; 
	  
	  private String username; 

	  private Role role ; 
	  
	  private boolean edit; 
}
