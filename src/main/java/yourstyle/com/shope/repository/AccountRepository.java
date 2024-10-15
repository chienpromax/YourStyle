package yourstyle.com.shope.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	@Query("SELECT ac FROM Account ac WHERE ac.username LIKE %?1% OR ac.email LIKE %?1%")
	Page<Account> findByUsernameOrEmail(String value, Pageable pageable);
	
//	@Query("SELECT ac FROM Account ac WHERE ac.username LIKE ?1 OR ac.email LIKE ?2")
	Account findByUsernameOrEmail(String username,String email);
}

