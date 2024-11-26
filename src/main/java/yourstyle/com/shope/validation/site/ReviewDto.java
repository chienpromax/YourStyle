package yourstyle.com.shope.validation.site;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import yourstyle.com.shope.model.Customer;
import yourstyle.com.shope.model.Product;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private Integer reviewId;
    private Integer rating;
    private String comment;
    private Timestamp createAt;
    private Timestamp updateAt;
    private Customer customer;
    private Product product;
    private List<String> images; // Danh sách các URL của ảnh

}
