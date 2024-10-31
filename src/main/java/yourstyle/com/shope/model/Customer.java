package yourstyle.com.shope.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
//@Entity
//@Table(name = "customers")
//public class Customer implements Serializable {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer customerId;
//
//    @Column(nullable = false, length = 50)
//    private String fullname;
//
//    @Column(nullable = true, length = 15)
//    private String phoneNumber;
//
//    @Column(nullable = true)
//    private Boolean gender;
//
//    @Column(length = 300)
//    private String avatar;
//
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    @Column(nullable = true)
//    @Temporal(TemporalType.DATE)
//    private Date birthday;
//
//    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
//    private Timestamp createDate = new Timestamp(System.currentTimeMillis());
//
//    @OneToOne
//    @JoinColumn(name = "accountId", referencedColumnName = "accountId", nullable = false)
//    private Account account;
//    @Transient // Để không lưu vào database
//    private MultipartFile imageFile;
//
//    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
//    private Customer customer;
//
//
//
//}
@Entity
@Table(name = "customers")
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;

    @Column(nullable = false, length = 50)
    private String fullname;

    @Column(nullable = true, length = 15)
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

    @Transient // Để không lưu vào database
    private MultipartFile imageFile;

    // Thêm trường isEdit với giá trị mặc định là false
    @Column(nullable = false)
    private Boolean isEdit = false;
}

