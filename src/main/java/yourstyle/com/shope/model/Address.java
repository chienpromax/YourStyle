package yourstyle.com.shope.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressId;

    // @ManyToOne
    // @JoinColumn(name = "customerId", nullable = false)
    // private Customer customer;

    @Column(name = "street", length = 255)
    private String street;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "ward", nullable = false)
    private String ward;

    @Column(name = "isDefault", columnDefinition = "BIT DEFAULT 0")
    private Boolean isDefault = false; // Địa chỉ mặc định

    @ManyToOne()
    @JoinColumn(name = "customerId", referencedColumnName = "customerId")
    @JsonBackReference
    private Customer customer;
}
