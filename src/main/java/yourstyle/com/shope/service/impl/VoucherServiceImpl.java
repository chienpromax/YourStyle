package yourstyle.com.shope.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.Exception.VoucherNotFoundException;
import yourstyle.com.shope.model.Voucher;
import yourstyle.com.shope.repository.VoucherRepository;
import yourstyle.com.shope.service.VoucherService;

@Service
public class VoucherServiceImpl implements VoucherService {
    @Autowired
    VoucherRepository voucherRepository;

    @Override
    public Voucher createVoucher(Voucher voucher) {
        return voucherRepository.save(voucher);
    }

    @Override
    public Page<Voucher> findAll(Pageable pageable) {
        return voucherRepository.findAll(pageable);
    }

    @Override
    public Page<Voucher> findByCodeOrName(String value, Pageable pageable) {
        return voucherRepository.findByCodeOrName(value, pageable);
    }

    @Override
    public Optional<Voucher> findByVoucherId(Integer voucherId) {
        return voucherRepository.findById(voucherId);
    }

    @Override
    public void deleteByVoucherId(Integer voucherId) {
        voucherRepository.deleteById(voucherId);
    }

    @Override
    public Page<Voucher> advancedSearch(String value, Boolean isPublic, Integer type, LocalDateTime fromDate,
            LocalDateTime toDate, Pageable pageable) {
        return voucherRepository.findByCriteria(value, isPublic, type, fromDate, toDate, pageable);
    }

    @Override
    public List<Voucher> findAll() {
        return voucherRepository.findAll();
    }

    @Override
    public Optional<Voucher> findByVoucherCode(String voucherCode) {
        return voucherRepository.findByVoucherCodeOrder(voucherCode);
    }

}
