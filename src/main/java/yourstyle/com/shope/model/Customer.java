package yourstyle.com.shope.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;

    @Column(nullable = false, length = 50)
    private String fullname;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = true)
    private Boolean gender;

    @Column(length = 300)
    private String avatar;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Date birthday;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createDate = new Timestamp(System.currentTimeMillis());

    @OneToOne
    @JoinColumn(name = "accountId", referencedColumnName = "accountId", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    @OneToMany(mappedBy = "customer")
    private List<Voucher> vouchers;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    private List<Address> addresses;
}
