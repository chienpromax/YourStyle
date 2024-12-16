package yourstyle.com.shope.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@Entity
@Table(name = "otp_tokens")
public class OTPToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "otp", nullable = false)
    private String otp;

    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;

    // Thêm mối quan hệ với Account
    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accountId")  // Sửa ở đây, thay "id" bằng "accountId"
    private Account account;

    // Constructor có tham số để lưu OTP
    public OTPToken(long id, String email, String otp, LocalDateTime expiryTime, Account account) {
        this.id = id;
        this.email = email;
        this.otp = otp;
        this.expiryTime = expiryTime;
        this.account = account;
    }

    // Constructor có tham số email, otp, và expiryTime
    public OTPToken(String email, String otp, LocalDateTime expiryTime) {
        this.email = email;
        this.otp = otp;
        this.expiryTime = expiryTime;
    }
}
