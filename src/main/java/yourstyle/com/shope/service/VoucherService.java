package yourstyle.com.shope.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import yourstyle.com.shope.model.Voucher;

public interface VoucherService {
    // Thêm một voucher mới
    Voucher createVoucher(Voucher voucher);

    Page<Voucher> findAll(Pageable pageable);

    Page<Voucher> findByCodeOrName(String value, Pageable pageable);

    Optional<Voucher> findByVoucherId(Integer voucherId);

    void deleteByVoucherId(Integer voucherId);

    Page<Voucher> advancedSearch(String value, Boolean isPublic, Integer type, LocalDateTime fromDate,
            LocalDateTime toDate, Pageable pageable);

    List<Voucher> findAll();

}
