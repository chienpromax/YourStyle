package yourstyle.com.shope.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    private Order order; // Liên kết với đối tượng Order

    // @Enumerated(EnumType.STRING) // Sử dụng EnumType.STRING để lưu giá trị trạng
    // thái dưới dạng chuỗi
    @Column(name = "status")
    private String status; // Trạng thái của đơn hàng

    @Column(name = "statusTime")
    private Timestamp statusTime; // Thời gian của trạng thái

}
