package yourstyle.com.shope.validation.admin;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderStatusHistoryDto {
    private Timestamp statusTime;
    private String statusText;
    // trạng thái đơn hàng
    private String statusDesciption;
    private String username;

    public OrderStatusHistoryDto(Timestamp statusTime, String statusText, String statusDesciption, String username) {
        this.statusTime = statusTime;
        this.statusText = statusText;
        this.statusDesciption = statusDesciption;
        this.username = username;
    }
}
