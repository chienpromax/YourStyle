package yourstyle.com.shope.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import yourstyle.com.shope.model.VoucherCustomer;

@Repository
public interface VoucherCustomerRepository extends JpaRepository<VoucherCustomer, Integer> {

    @Modifying
    @Transactional // Đánh dấu phương thức này là một transaction
    @Query("DELETE FROM VoucherCustomer vc WHERE vc.voucher.voucherId = ?1")
    void deleteByVoucherId(Integer voucherId);

    @Query("SELECT CASE WHEN COUNT(vc) > 0 THEN TRUE ELSE FALSE END FROM VoucherCustomer vc " +
            "WHERE vc.customer.customerId = ?1 AND vc.voucher.voucherId = ?2")
    boolean existsByCustomerIdAndVoucherId(Integer customerId, Integer voucherId);

    @Modifying
    @Transactional // Đảm bảo rằng việc xóa sẽ thực hiện trong một transaction
    @Query("DELETE FROM VoucherCustomer vc WHERE vc.customer.customerId = ?1 AND vc.voucher.voucherId = ?2")
    void deleteByCustomerIdAndVoucherId(Integer customerId, Integer voucherId);

    List<VoucherCustomer> findByVoucher_VoucherId(Integer voucherId);

}
