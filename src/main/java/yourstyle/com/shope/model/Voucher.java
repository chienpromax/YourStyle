package yourstyle.com.shope.model;

import java.util.List;
import java.io.Serializable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;

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

    @Column(nullable = false, length = 255)
    private String voucherCode;

    @Column(nullable = false, length = 255)
    private String voucherName;

    @Column(columnDefinition = "TEXT")
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
    private Byte type;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createAt = new Timestamp(System.currentTimeMillis());

    private Boolean isPublic;

    @ManyToOne
    @JoinColumn(name = "createBy", nullable = false)
    private Account account;

    @JsonIgnore
    @OneToMany(mappedBy = "voucher", fetch = FetchType.EAGER)
    List<VoucherCustomer> voucherCustomers;

}
