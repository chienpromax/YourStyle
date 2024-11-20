package yourstyle.com.shope.validation.admin;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yourstyle.com.shope.model.Customer;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto implements Serializable {
    private Integer addressId;
    @NotBlank(message = "Vui lòng nhập địa chỉ cụ thể!")
    private String street;
    private String ward;
    private String district;
    private String city;
    private boolean isDefault = false;
    private Integer customerId;
    private Customer customer;
    // thêm khách hàng sell
    private String fullname;
    private String phoneNumber;
    private boolean gender;
    private String email;

    public AddressDto(Integer addressId, String street, String ward, String district, String city, Customer customer,
            boolean isDefault) {
        this.addressId = addressId;
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.city = city;
        this.customer = customer;
        this.isDefault = isDefault;
    }

    public AddressDto(String fullname, String phoneNumber, boolean gender, String street, String ward, String district,
            String city) {
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.city = city;
    }

    // chọn khách hàng
    public AddressDto(String fullname, String phoneNumber, String street, String ward, String district,
            String city) {
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.city = city;
    }
}
