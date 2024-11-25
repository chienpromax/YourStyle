window.addEventListener("DOMContentLoaded", function () {
    // tìm kiếm đơn hàng
    document.getElementById("searchOrderListForm").addEventListener("submit", function (e) {
        e.preventDefault();
        const value = document.getElementById("value").value.trim();
        fetch(`/admin/orders/search?value=${value}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Lỗi: ", error);
                }
                return response.text(); // Bạn có thể dùng response.text() nếu chỉ cần thông báo
            })
            .then((html) => {
                const tbody = document.querySelector(".dsorder");
                tbody.innerHTML = html;
            })
            .catch((err) => {
                console.error("Lỗi:", err);
            });
    });
    // lọc đơn hàng theo trạng thái
    const statusSelectElement = document.getElementById("statusSelect");
    statusSelectElement.addEventListener("change", () => {
        const statusValue = statusSelectElement.value;
        fetch(`/admin/orders/filterOrder?status=${statusValue}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Lỗi: ", error);
                }
                return response.text(); // Bạn có thể dùng response.text() nếu chỉ cần thông báo
            })
            .then((html) => {
                const tbody = document.querySelector(".dsorder");
                tbody.innerHTML = html;
            })
            .catch((err) => {
                console.error("Lỗi:", err);
            });
    });
    // lọc đơn hàng theo kênh mua hàng
    const orderChannelSelectElement = document.getElementById("orderChannelSelect");
    orderChannelSelectElement.addEventListener("change", function () {
        const orderChannelValue = orderChannelSelectElement.value;
        fetch(`/admin/orders/filterOrder?orderChannel=${orderChannelValue}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Lỗi: ", error);
                }
                return response.text(); // Bạn có thể dùng response.text() nếu chỉ cần thông báo
            })
            .then((html) => {
                const tbody = document.querySelector(".dsorder");
                tbody.innerHTML = html;
            })
            .catch((err) => {
                console.error("Lỗi:", err);
            });
    });
});
// xử lý lọc từ ngày đến ngày đơn hàng
function filterOrders() {
    const fromDate = document.getElementById("datetimeFrom").value;
    const toDate = document.getElementById("datetimeTo").value;
    if (fromDate && toDate) {
        if (new Date(fromDate) <= new Date(toDate)) {
            fetch(`/admin/orders/filterOrder?from_date=${fromDate}&to_date=${toDate}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            })
                .then((response) => {
                    if (!response.ok) {
                        throw new Error("Lỗi: ", error);
                    }
                    return response.text(); // Bạn có thể dùng response.text() nếu chỉ cần thông báo
                })
                .then((html) => {
                    const tbody = document.querySelector(".dsorder");
                    tbody.innerHTML = html;
                })
                .catch((err) => {
                    console.error("Lỗi:", err);
                });
        }
    }
}
