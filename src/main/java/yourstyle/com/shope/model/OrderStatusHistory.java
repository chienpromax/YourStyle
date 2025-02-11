package yourstyle.com.shope.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orderStatusHistories")
public class OrderStatusHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderStatusHistoryId;

    @ManyToOne
    @JoinColumn(name = "orderId", referencedColumnName = "orderId", nullable = false)
    private Order order;
    // Trạng thái của đơn hàng
    @Column(name = "status")
    private String status;
    // Thời gian của trạng thái
    @Column(name = "statusTime")
    private Timestamp statusTime;

    @Transient
    public String getStatusDescription() {
        return OrderStatus.getDesciptionFromOrderStatus(this.status);
    }

}
