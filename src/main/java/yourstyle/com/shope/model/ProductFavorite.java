package yourstyle.com.shope.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productFavorites")
public class ProductFavorite {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int favoriteId;

	@Column(name = "customerId", nullable = false)
	private int customerId;

	// Chỗ ni map product cho tiện nè 
	// lấy lấy ra product ở favourite product luôn á
	// Trình thấy do set mối quan hệ trong class JPA nó hỗ trợ sẵn 
	@Column(name = "productId", nullable = false)
	private int productId;

	@Column(name = "timeAt", nullable = false)
	private Timestamp timeAt;

}
