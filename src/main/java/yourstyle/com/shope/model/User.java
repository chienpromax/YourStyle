package yourstyle.com.shope.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String username;
	private String dateOfBirth;
	private String gender;
	private String nationality;
	private String phoneNumber;

	@Column(nullable = false, length = 255)
	private String email;

	// Thay đổi trường email để liên kết với Account
	@OneToOne
	@JoinColumn(name = "accountId", referencedColumnName = "accountId")
	private Account account;

	// Thêm trường avatar
	private String avatar;

	// Lấy email từ User
	public String getEmailFromUser(User user) {
		return user.getAccount().getEmail(); // Lấy email từ đối tượng Account
	}

}
