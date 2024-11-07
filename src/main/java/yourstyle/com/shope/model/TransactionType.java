package yourstyle.com.shope.model;

public enum TransactionType {
    ONLINE("Online"),
    BANK_TRANSFER("Chuyển khoản"),
    COD("Thanh toán khi nhận hàng"),
    E_WALLET("Ví điện tử"),
    CREDIT_CARD("Thẻ tín dụng"),
    PAYMENT_GATEWAY("Cổng thanh toán trực tuyến"),
    IN_STORE("Thanh toán tại quầy");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
