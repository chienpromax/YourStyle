package yourstyle.com.shope.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vouchers")
public class Voucher implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer voucherId;
    @Column(nullable = false)
    private String voucherCode;
    @Column(nullable = false)
    private String voucherName;
    private String description;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minTotalAmount;
    @Column(precision = 10, scale = 2)
    private BigDecimal maxTotalAmount;
    @Column(nullable = false)
    private Integer maxUses;
    @Column(nullable = false)
    private Integer maxUsesUser;
    @Column(nullable = false)
    private Short type;
    private Timestamp startDate = new Timestamp(System.currentTimeMillis());
    private Timestamp endDate;
    private Boolean isPublic;
    private Timestamp createAt = new Timestamp(System.currentTimeMillis());
    @ManyToOne
    @JoinColumn(name = "accountId", referencedColumnName = "accountId")
    private Account account;
    @ManyToOne
    @JoinColumn(name = "customerId", referencedColumnName = "customerId")
    private Customer customer;
    @OneToMany(mappedBy = "voucher")
    private List<Order> orders;
    @JsonIgnore
    @OneToMany(mappedBy = "voucher", fetch = FetchType.EAGER)
    List<VoucherCustomer> voucherCustomers;

}
