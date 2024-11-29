package yourstyle.com.shope.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import yourstyle.com.shope.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	// Phương thức tìm người dùng theo username
    User findByUsername(String username);

    // Phương thức kiểm tra người dùng có tồn tại không
    boolean existsByUsername(String username);
    
    Optional<User> findByAccount_AccountId(Integer accountId);
    
    boolean existsByEmail(String email);
}
