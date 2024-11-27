window.addEventListener("DOMContentLoaded", function () {
    window.returnOrder = function () {
        const orderId = document.getElementById("orderId").value.trim();
        const orderIdParseInt = parseInt(orderId);
        if (orderIdParseInt) {
            Swal.fire({
                title: "Bạn có chắc chắn muốn trả hàng?",
                text: `Mã đơn hàng ${orderId} sẽ được hoàn trả`,
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Xác nhận",
                cancelButtonText: "Hủy",
            }).then((result) => {
                if (result.isConfirmed) {
                    fetch(`/admin/returnOrder/updateStatus?orderId=${orderIdParseInt}`, {
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

                            // kiểm tra xem có lỗi không
                            const errorElement = document.querySelector(".alert.alert-danger");
                            if (errorElement) {
                                createToast("error", "fa-solid fa-circle-exclamation", "Lỗi", errorElement.textContent);
                                document.getElementById("orderId").value = "";
                            } else {
                                createToast("success", "fa-solid fa-circle-check", "Thành công", "Trả hàng thành công");
                                document.getElementById("orderId").value = "";
                            }
                        })
                        .catch((err) => {
                            createToast("error", "fa-solid fa-circle-exclamation", "Lỗi", "Trả hàng thất bại");
                            console.error("Lỗi:", err);
                        });
                }
            });
        } else {
            createToast("error", "fa-solid fa-circle-exclamation", "Lỗi", "Vui lòng nhập mã hóa đơn!!");
            document.getElementById("orderId").focus();
        }
    };

    function createToast(type, icon, title, text) {
        const newToast = document.createElement("div");
        newToast.innerHTML = `<div class="toast ${type}">
        <i class="${icon}"></i>
        <div class="content">
            <div class="title">${title}</div>
            <span>${text}</span>
        </div>
        <i class="close fa-solid fa-xmark" onclick="(this.parentElement).remove()"></i>
    </div>`;
        const displayNotifications = document.querySelector(".displayNotifications");
        displayNotifications.appendChild(newToast);
        setTimeout(() => {
            newToast.remove();
        }, 5000);
    }
});
