package yourstyle.com.shope.model;

public enum OrderChannel {
    ONLINE("ONLINE", "Trực tuyến"), // Đặt hàng trực tuyến
    DIRECT("DIRECT", "Trực tiếp"), // Đặt hàng trực tiếp
    IN_STORE("IN_STORE", "Tại quầy"); // Đặt hàng tại quầy

    private final String value;
    private final String name;

    OrderChannel(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
