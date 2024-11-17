package yourstyle.com.shope.validation.admin;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto implements Serializable {
    private Integer orderDetailId;
    private BigDecimal discountPrice;
    private Integer quantity;
    private Integer orderId;
    private Integer productVariantId;
    private Integer sizeId;
    private Integer colorId;
    // hiển thị danh sách sản phẩm trong đơn bán hàng
    private String image;
    private String name;// tên sản phẩm
    private String colorName;
    private String sizeName;
    private String totalSum;
    private String formattedTotalAmount;
    private String oldPrice;
    private String newPrice;
    private boolean discount;
    private String discountPercent;
    //
    private String voucherCode;
    private Short discountType;
    private String formattedDiscount;

    // giảm SL
    public OrderDetailDto(Integer orderId, Integer productVariantId, Integer colorId, Integer sizeId) {
        this.orderId = orderId;
        this.productVariantId = productVariantId;
        this.colorId = colorId;
        this.sizeId = sizeId;
    }

    public OrderDetailDto(String voucherCode, Short discountType, String formattedDiscount) {
        this.voucherCode = voucherCode;
        this.discountType = discountType;
        this.formattedDiscount = formattedDiscount;
    }

    public OrderDetailDto(Integer productVariantId, Integer colorId, Integer sizeId, String image, String name,
            String colorName, String sizeName,
            String oldPrice, String newPrice, Integer quantity, String totalSum, String formattedTotalAmount,
            boolean discount,
            Integer orderDetailId, String discountPercent) {
        this.productVariantId = productVariantId;
        this.colorId = colorId;
        this.sizeId = sizeId;
        this.image = image;
        this.name = name;
        this.colorName = colorName;
        this.sizeName = sizeName;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.quantity = quantity;
        this.totalSum = totalSum;
        this.formattedTotalAmount = formattedTotalAmount;
        this.discount = discount;
        this.orderDetailId = orderDetailId;
        this.discountPercent = discountPercent;
    }

    public OrderDetailDto(Integer orderId, Integer productVariantId, BigDecimal discountPrice,
            Integer quantity, Integer sizeId, Integer colorId) {
        this.orderId = orderId;
        this.productVariantId = productVariantId;
        this.discountPrice = discountPrice;
        this.quantity = quantity;
        this.sizeId = sizeId;
        this.colorId = colorId;
    }

}
