package yourstyle.com.shope.model;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.*;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productvariants")
public class ProductVariant implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productVariantId;
    @Column(nullable = false)
    private Integer quantity;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createAt;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updateAt;
    @ManyToOne
    @JoinColumn(name = "sizeId", referencedColumnName = "sizeId")
    @JsonBackReference
    private Size size;
    @ManyToOne
    @JoinColumn(name = "colorId", referencedColumnName = "colorId")
    @JsonBackReference
    private Color color;
    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    @JsonBackReference
    private Product product;
    @OneToMany(mappedBy = "productVariant")
    @JsonManagedReference
    private List<OrderDetail> orderdetails;

    @PrePersist
    protected void onCreate() {
        createAt = new Timestamp(System.currentTimeMillis());
        updateAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = new Timestamp(System.currentTimeMillis());
    }
}
