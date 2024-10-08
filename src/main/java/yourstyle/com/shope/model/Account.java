package yourstyle.com.shope.model;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
public class Account implements Serializable{
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private int accountId;
	@Column(nullable = false,length = 50,unique = true)
	private String username;
	@Column(nullable = false,length = 255)
	private String password;
	@Column(nullable = false,length = 255,unique = true)
	private String email;
	@Column(name = "status")
	private Boolean status = true;
	@Column(nullable = false,columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp createAt;
	@OneToOne
	@JoinColumn(name = "roleId",referencedColumnName = "roleId")
	private Role role;
}
