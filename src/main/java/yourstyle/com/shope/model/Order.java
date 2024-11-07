package yourstyle.com.shope.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;



@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp orderDate = new Timestamp(System.currentTimeMillis());
    private int status = 1;
    @Column(nullable = true)
    private String note;
    @Enumerated(EnumType.STRING)
    @Column(name = "transactionType")
    private TransactionType transactionType;
    private String paymentMethod;
    private String transactionStatus;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp statusUpdatedAt;

    @ManyToOne
    @JoinColumn(name = "customerId", referencedColumnName = "customerId", nullable = false)
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "voucherId", referencedColumnName = "voucherId", nullable = true)
    private Voucher voucher;
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private List<OrderDetail> orderDetails;

    public OrderStatus getStatus() {
        return OrderStatus.fromCode(status);
    }

    public void setStatus(OrderStatus orderStatus) {
        this.status = orderStatus.getCode();
    }

    public String getStatusDescription() {
        return OrderStatus.fromCode(this.status).getDescription();
    }

    @PreUpdate
    protected void onUpdate() {
        statusUpdatedAt = new Timestamp(System.currentTimeMillis());
    }
}
