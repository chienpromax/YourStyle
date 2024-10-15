package yourstyle.com.shope.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import yourstyle.com.shope.model.Voucher;

public interface VoucherService {
    // Thêm một voucher mới
    Voucher createVoucher(Voucher voucher);

    Page<Voucher> findAll(Pageable pageable);
}
