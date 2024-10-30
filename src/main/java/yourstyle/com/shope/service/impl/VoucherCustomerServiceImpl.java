package yourstyle.com.shope.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.VoucherCustomer;
import yourstyle.com.shope.repository.VoucherCustomerRepository;
import yourstyle.com.shope.service.VoucherCustomerService;

@Service
public class VoucherCustomerServiceImpl implements VoucherCustomerService {
    @Autowired
    VoucherCustomerRepository voucherCustomerRepository;

    @Override
    public void save(VoucherCustomer voucherCustomer) {
        voucherCustomerRepository.save(voucherCustomer);
    }

    @Override
    public void deleteByVoucherId(Integer voucherId) {
        voucherCustomerRepository.deleteByVoucherId(voucherId);
    }

    @Override
    public boolean existsByCustomerIdAndVoucherId(Integer customerId, Integer voucherId) {
        return voucherCustomerRepository.existsByCustomerIdAndVoucherId(customerId, voucherId);
    }

    @Override
    public void deleteByCustomerIdAndVoucherId(Integer customerId, Integer voucherId) {
        voucherCustomerRepository.deleteByCustomerIdAndVoucherId(customerId, voucherId);
    }

    @Override
    public List<VoucherCustomer> findByVoucher_VoucherId(Integer voucherId) {
        return voucherCustomerRepository.findByVoucher_VoucherId(voucherId);
    }

}
