package yourstyle.com.shope.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Slide {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int slideId; // Tự động tăng

	@Column(length = 1000) 
	private String imagePaths; 
	

	public String[] getImagePathsArray() {
		return imagePaths.split(",");
	}

	public Slide(String imagePaths) {
		super();
		this.imagePaths = imagePaths;
	}

	@Transient 
	private String[] imagePathsArray; // Mảng ảnh tách ra từ imagePaths
}
