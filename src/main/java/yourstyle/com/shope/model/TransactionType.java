package yourstyle.com.shope.model;

public enum TransactionType {
    ONLINE("Trực tuyến"),
    BANK_TRANSFER("Chuyển khoản"),
    COD("Thanh toán khi nhận hàng"),
    E_WALLET("Ví điện tử"),
    CREDIT_CARD("Thẻ tín dụng"),
    PAYMENT_GATEWAY("Cổng thanh toán trực tuyến"),
    PAY_IN_STORE("Thanh toán tại quầy"),
    CASH("Tiền mặt");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
