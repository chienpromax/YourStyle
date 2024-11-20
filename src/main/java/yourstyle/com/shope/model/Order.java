package yourstyle.com.shope.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private Timestamp orderDate;

    @Column(nullable = false)
    private Integer status;

    private String note;

    private Timestamp statusUpdatedAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TransactionType transactionType;

    private String paymentMethod;

    private String transactionStatus;

    private Timestamp transactionTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private OrderChannel orderChannel;

    @ManyToOne
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "voucherId")
    private Voucher voucher;

    @JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    // Getters and setters

    public enum TransactionType {
        BANK_TRANSFER, COD, CREDIT_CARD, E_WALLET, ONLINE, PAYMENT_GATEWAY, PAY_IN_STORE
    }

    public enum OrderChannel {
        ONLINE, DIRECT, IN_STORE
    }
}
