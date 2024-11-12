package yourstyle.com.shope.model;

public enum OrderStatus {
    ALL_ORDERS(7, "Tất cả"),
    CANCELED(0, "Đơn hàng bị hủy"),
    PLACED(1, "Đã đặt hàng"),
    PACKING(2, "Đang đóng gói"),
    SHIPPED(3, "Đã giao cho vận chuyển"),
    IN_TRANSIT(4, "Đang giao hàng"),
    PAID(5, "Đã thanh toán"),
    COMPLETED(6, "Hoàn thành"),
    RETURNED(8, "Trả hàng"),
    Buying(9, "Đang đặt");

    private final int code;
    private final String description;

    OrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Lỗi code: " + code);
    }

    public static OrderStatus[] getALLOrderStatus() {
        return OrderStatus.values();
    }
}
