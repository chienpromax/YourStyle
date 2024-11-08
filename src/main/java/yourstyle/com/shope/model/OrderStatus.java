package yourstyle.com.shope.model;

public enum OrderStatus {
    ALL_ORDERS(7, "Tất cả"),
    CANCELED(0, "Đơn hàng bị hủy"),
    PLACED(1, "Đã đặt hàng"),
    PACKING(2, "Đang đóng gói"),
    SHIPPED(3, "Đã giao cho vận chuyển"),
    IN_TRANSIT(4, "Đang giao hàng"),
    COMPLETED(5, "Hoàn thành"),
    RETURNED(6, "Trả hàng"),
    PAID(8, "Đã thanh toán");

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
