package yourstyle.com.shope.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import yourstyle.com.shope.model.OTPToken;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTPToken, Long> {
 // Tìm OTP theo email
 Optional<OTPToken> findByEmail(String email);

 // Tìm OTP theo mã OTP
 Optional<OTPToken> findByOtp(String otp);

 Optional<OTPToken> findByEmailAndOtp(String email, String otp);
}