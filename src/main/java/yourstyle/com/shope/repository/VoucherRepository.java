package yourstyle.com.shope.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Voucher;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {

        @Query("SELECT v FROM Voucher v WHERE v.voucherCode LIKE %?1% OR v.voucherName LIKE %?1%")
        Page<Voucher> findByCodeOrName(String value, Pageable pageable);

        @Query("SELECT v FROM Voucher v WHERE v.voucherCode LIKE ?1")
        Voucher findByVoucherCode(String voucherCode);

        @Query("SELECT v FROM Voucher v WHERE " +
                        "(:value IS NULL OR v.voucherCode LIKE %:value% OR v.voucherName LIKE %:value%) AND " +
                        "(:isPublic IS NULL OR v.isPublic = :isPublic) AND " +
                        "(:type IS NULL OR v.type = :type) AND " +
                        "(:fromDate IS NULL OR v.startDate >= :fromDate) AND " +
                        "(:toDate IS NULL OR v.endDate <= :toDate)")
        Page<Voucher> findByCriteria(@Param("value") String value,
                        @Param("isPublic") Boolean isPublic,
                        @Param("type") Integer type,
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate,
                        Pageable pageable);

        @Query("SELECT v FROM Voucher v WHERE v.voucherCode = ?1")
        Optional<Voucher> findByVoucherCodeOrder(String voucherCode);

        @Query("SELECT v FROM Voucher v WHERE :totalAmount >= v.minTotalAmount AND (:totalAmount <= v.maxTotalAmount OR v.maxTotalAmount IS NULL)")
        List<Voucher> findVouchersByTotalAmount(@Param("totalAmount") BigDecimal totalAmount);

}
