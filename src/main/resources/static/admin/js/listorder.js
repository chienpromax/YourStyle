window.addEventListener("DOMContentLoaded", function () {
    const statusSelectElement = document.getElementById("statusSelect");
    const orderChannelSelectElement = document.getElementById("orderChannelSelect");
    statusSelectElement.addEventListener("change", () => {
        const statusValue = statusSelectElement.value;
        window.location.href = `/admin/orders?status=${statusValue}`;
    });
    orderChannelSelectElement.addEventListener("change", function () {
        const orderChannelValue = orderChannelSelectElement.value;
        window.location.href = `/admin/orders?orderChannel=${orderChannelValue}`;
    });
});
// xử lý lọc từ ngày đến ngày đơn hàng
function filterOrders() {
    const fromDate = document.getElementById("datetimeFrom").value;
    const toDate = document.getElementById("datetimeTo").value;
    if (fromDate && toDate) {
        if (new Date(fromDate) <= new Date(toDate)) {
            window.location.href = `/admin/orders?from_date=${fromDate}&to_date=${toDate}`;
        }
    }
}
