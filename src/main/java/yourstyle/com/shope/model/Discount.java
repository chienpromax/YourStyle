package yourstyle.com.shope.model;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.*;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.sql.*;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "discounts")
public class Discount implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer discountId;
    @Column(length = 255)
    private String discountName;
    private String description;
    @Column(precision = 10, scale = 2)
    private BigDecimal discountPercent;
    private Timestamp startDate;
    private Timestamp endDate;
    @OneToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    @JsonBackReference
    private Product product;
}
