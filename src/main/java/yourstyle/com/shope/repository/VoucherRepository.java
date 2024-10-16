package yourstyle.com.shope.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Voucher;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {

    @Query("SELECT v FROM Voucher v WHERE v.voucherCode LIKE %?1% OR v.voucherName LIKE %?1%")
    Page<Voucher> findByCodeOrName(String value, Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE v.voucherCode LIKE ?1")
    Voucher findByVoucherCode(String voucherCode);

}
