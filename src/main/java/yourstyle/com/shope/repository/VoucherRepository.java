package yourstyle.com.shope.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Voucher;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {

}
