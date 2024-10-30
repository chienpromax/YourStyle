package yourstyle.com.shope.service;

import java.util.List;

import yourstyle.com.shope.model.VoucherCustomer;

public interface VoucherCustomerService {

    void save(VoucherCustomer voucherCustomer);

    void deleteByVoucherId(Integer voucherId);

    boolean existsByCustomerIdAndVoucherId(Integer customerId, Integer voucherId);

    void deleteByCustomerIdAndVoucherId(Integer customerId, Integer voucherId);

    List<VoucherCustomer> findByVoucher_VoucherId(Integer voucherId);

}
