package yourstyle.com.shope.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.Role;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	@Query("SELECT ac FROM Account ac WHERE ac.username LIKE %?1% OR ac.email LIKE %?1%")
	Page<Account> findByUsernameOrEmail(String value, Pageable pageable);

	// @Query("SELECT ac FROM Account ac WHERE ac.username LIKE ?1 OR ac.email LIKE
	// ?2")
	Account findByUsernameOrEmail(String username, String email);

	Optional<Account> findByUsername(String username);

	@Query("SELECT a FROM Account a WHERE a.role.name = :roleName")
	List<Account> findAccountsByRoleName(String roleName);

	Optional<Account> findByEmail(String email);

	Optional<Account> findByToken(String token);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	Account findByResetToken(String resetToken);

	@Modifying
	@Transactional
	@Query("UPDATE Account a SET a.email = ?2 WHERE a.username = ?1")
	boolean updateEmailByUsername(String username, String email);
}
