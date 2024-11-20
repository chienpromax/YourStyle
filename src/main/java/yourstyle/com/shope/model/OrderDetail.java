package yourstyle.com.shope.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.*;
import java.sql.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orderdetails")
public class OrderDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderDetailId;
    @Column(nullable = true, precision = 10, scale = 2)
    private BigDecimal price;
    @Column(nullable = true)
    private Integer quantity;
    private Timestamp createAt;
    private Timestamp updateAt;
    @ManyToOne
    @JoinColumn(name = "orderId", referencedColumnName = "orderId", nullable = false)
    private Order order;
    @ManyToOne
    @JoinColumn(name = "productVariantId", referencedColumnName = "productVariantId", nullable = true)
    private ProductVariant productVariant;

    @PrePersist
    public void onCreate() {
        createAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = new Timestamp(System.currentTimeMillis());
    }
}
