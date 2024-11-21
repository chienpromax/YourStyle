package yourstyle.com.shope.model;

import jakarta.persistence.CascadeType;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer accountId;
	@Column(nullable = false, length = 50, unique = true)
	private String username;
	@Column(nullable = false, length = 255)
	private String password;
	@Column(nullable = false, length = 255, unique = true)
	private String email;
	@Column(name = "status")
	private Boolean status = true;
	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp createAt = new Timestamp(System.currentTimeMillis());
	@ManyToOne
	@JoinColumn(name = "roleId", referencedColumnName = "roleId")
	private Role role;
	// Thêm thuộc tính token để lưu trữ mã đặt lại mật khẩu
	private String token;

	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
	private Customer customer;
	@Column(name = "reset_token")
	private String resetToken;

	// @OneToMany(mappedBy = "account")
	// private List<Voucher> vouchers;

	@OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
	private List<Voucher> vouchers;

}
