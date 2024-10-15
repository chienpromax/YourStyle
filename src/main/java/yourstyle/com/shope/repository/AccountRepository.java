package yourstyle.com.shope.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

}
