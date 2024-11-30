package yourstyle.com.shope.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer customerId;

	@Column(nullable = true, length = 50)
	private String fullname;

	@Column(nullable = false, length = 15)
	private String phoneNumber;

	@Column(nullable = true)
	private Boolean gender;

	@Column(length = 300)
	private String avatar;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(nullable = true)
	@Temporal(TemporalType.DATE)
	private Date birthday;

	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp createDate = new Timestamp(System.currentTimeMillis());

	@OneToOne
	@JoinColumn(name = "accountId", referencedColumnName = "accountId", nullable = false)
	@JsonIgnore
	private Account account;

	// @JsonIgnore
	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
	@JsonManagedReference
	private List<Address> addresses;

	@JsonIgnore
	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
	private List<Order> orders;
	@JsonIgnore
	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
	private List<Voucher> vouchers;

	private boolean isEdit = false;

	@Transient // Để không lưu vào database
	private MultipartFile imageFile;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Customer customer = (Customer) obj;
		return customerId != null && customerId.equals(customer.customerId);
	}

	@Override
	public int hashCode() {
		return customerId != null ? customerId.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "Customer{" + "customerId=" + customerId + ", fullname='" + fullname + '\'' + ", phoneNumber='"
				+ phoneNumber + '\'' + ", birthday=" + birthday + ", gender=" + gender + ", nationality='" + nationality
				+ '\'' + '}';
	}

	public Address getDefaultAddress() {
		if (addresses == null || addresses.isEmpty()) {
			return new Address();
		}
		return addresses.stream().filter(Address::getIsDefault).findFirst().orElse(new Address());
	}
	
	// new
	@Column(nullable = true, length = 100) // Thêm trường nationality
	private String nationality;

	// Lấy email từ account
		public String getEmailFromUser(Customer customer) {
			return customer.getAccount().getEmail(); // Lấy email từ đối tượng Account
		}

}
