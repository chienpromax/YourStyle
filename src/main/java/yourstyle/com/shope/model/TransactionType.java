package yourstyle.com.shope.model;

public enum TransactionType {
    OnlinePayment("Thanh toán online"),
    CashOnDelivery("Thanh toán khi nhận hàng"),
    PayInStore("Thanh toán tại quầy");

    private final String code;

    TransactionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static TransactionType fromString(String code) {
        for (TransactionType transaction : TransactionType.values()) {
            if (transaction.code.equalsIgnoreCase(code)) {
                return transaction;
            }
        }
        return null;
    }
}
