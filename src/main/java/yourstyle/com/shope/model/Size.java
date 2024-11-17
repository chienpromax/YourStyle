package yourstyle.com.shope.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sizes")
public class Size implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sizeId;
    @Column(nullable = false, length = 5, unique = true)
    private String sizeName;
    @OneToMany(mappedBy = "size")
    private List<ProductVariant> productVariants;
}
