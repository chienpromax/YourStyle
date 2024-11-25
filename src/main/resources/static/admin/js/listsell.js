import { format } from "https://cdn.skypack.dev/date-fns";
window.addEventListener("DOMContentLoaded", () => {
    document.getElementById("btnCreateOrder").addEventListener("click", () => {
        Swal.fire({
            title: "Xác nhận",
            text: "Tạo đơn tại quầy?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Xác nhận",
            cancelButtonText: "Hủy",
        }).then((result) => {
            if (result.isConfirmed) {
                fetch("/api/admin/sell/saveOrUpdate", {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                    },
                })
                    .then((reponse) => {
                        if (!reponse.ok) {
                            throw new Error("Lỗi khi tạo đơn!");
                        }
                        return reponse.json();
                    })
                    .then((data) => {
                        const tbody = document.querySelector(".listOrderInStore");
                        tbody.innerHTML = "";
                        let rows = "";
                        data.forEach((order, index) => {
                            const {
                                orderId,
                                fullname,
                                totalQuantities,
                                totalAmounts,
                                orderDate,
                                orderChannel,
                                status,
                            } = order;
                            const formatedOrderDate = orderDate
                                ? format(new Date(orderDate), "dd-MM-yyyy HH:mm:ss")
                                : "dd-MM-yyyy HH:mm:ss";
                            const totalQuantity = totalQuantities[orderId] || 0;
                            const totalAmount = totalAmounts[orderId] || "0.000 VND";
                            rows += `
                            <tr>
                                <td>${index + 1}</td>
                                <td>${orderId}</td>
                                <td>${fullname}</td>
                                <td>${totalQuantity}</td>
                                <td>${totalAmount}</td>
                                <td>${formatedOrderDate}</td>
                                <td>${orderChannel}</td>
                                <td>
                                    <span class="badge bg-success">
                                        ${status}
                                    </span>
                                </td>
                                <td>
                                    <a
                                        href="/admin/sell/detail/${orderId}"
                                    >
                                        <i class="bi bi-eye"></i>
                                    </a>
                                </td>
                            </tr>
                            `;
                        });
                        tbody.innerHTML = rows;
                        createToast("success", "fa-solid fa-circle-check", "Thành công", "Tạo đơn thành công");
                    })
                    .catch((err) => {
                        console.log("có lỗi xảy ra: " + err);
                        createToast("error", "fa-solid fa-circle-exclamation", "Lỗi", "Tạo đơn thất bại!");
                    });
            }
        });
    });

    document.getElementById("value").addEventListener("input", function (e) {
        e.preventDefault();
        const value = document.getElementById("value").value.trim();
        // console.log(value);

        fetch(`/admin/sell/searchListOrderInStore?value=${value}`, {
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
                const tbody = document.querySelector(".listOrderInStore");
                tbody.innerHTML = html;
            })
            .catch((err) => {
                console.error("Lỗi:", err);
            });
    });
    window.createToast = function (type, icon, title, text) {
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
    };
});
