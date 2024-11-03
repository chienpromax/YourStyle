window.addEventListener("DOMContentLoaded", () => {
    const orderElement = document.getElementById("orderId");
    const orderId = orderElement.textContent;
    const steps = document.querySelectorAll("#progressbar-2 li");
    document.getElementById("btnConfirm").addEventListener("click", () => {
        let currentStatus = 1;
        steps.forEach((step) => {
            if (step.classList.contains("active")) {
                currentStatus = parseInt(step.getAttribute("data-status"));
            }
        });
        if (currentStatus < steps.length) {
            const nextStatus = currentStatus + 1;
            UpdateUI(nextStatus);

            Swal.fire({
                title: "Bạn có chắc chắn muốn thay đổi trạng thái?",
                text: "Trạng thái sẽ được cập nhật",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Xác nhận",
                cancelButtonText: "Hủy",
            }).then((result) => {
                if (result.isConfirmed) {
                    // gọi hàm cập nhật trạng thái
                    updateStatus(orderId, nextStatus);
                }
            });
        }
    });
    document.getElementById("btnPreviousStatus").addEventListener("click", () => {
        let currentStatus = 1;
        steps.forEach((step) => {
            if (step.classList.contains("active")) {
                currentStatus = parseInt(step.getAttribute("data-status"));
            }
        });
        if (currentStatus > 1) {
            const previousStatus = currentStatus - 1;
            UpdateUI(previousStatus);
            Swal.fire({
                title: "Bạn có chắc chắn muốn thay đổi trạng thái?",
                text: "Trạng thái sẽ được cập nhật",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: "Xác nhận",
                cancelButtonText: "Hủy",
            }).then((result) => {
                if (result.isConfirmed) {
                    // gọi hàm cập nhật trạng thái
                    updateStatus(orderId, previousStatus);
                }
            });
        }
    });
    document.getElementById("btnCancel").addEventListener("click", () => {
        const cancelStatus = 0;
        Swal.fire({
            title: "Bạn có chắc chắn muốn thay đổi trạng thái?",
            text: "Trạng thái sẽ được cập nhật",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Xác nhận",
            cancelButtonText: "Hủy",
        }).then((result) => {
            if (result.isConfirmed) {
                // gọi hàm cập nhật trạng thái
                updateStatus(orderId, cancelStatus);
                // cập nhật hình ảnh
                const cardStatusElement = document.getElementById("cardStatus");
                cardStatusElement.innerHTML = "";
                const newStatusElement = document.createElement("div");
                newStatusElement.innerHTML = `<div class="order-status text-danger text-center">
        <i class="bi bi-x-circle-fill"></i>
        <p >Đơn hàng ${orderId} đã bị hủy</p>
        </div>`;
                cardStatusElement.appendChild(newStatusElement);
            }
        });
    });
    function updateStatus(orderId, status) {
        fetch("http://localhost:8080/api/admin/orders/update-status", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ orderId: orderId, status: status }),
        })
            .then((reponse) => {
                if (reponse.ok) {
                    return reponse.text();
                } else {
                    throw new Error("Cập nhật trạng thái thất bại!");
                }
            })
            .then((message) => {
                createToast("success", "fa-solid fa-circle-check", "thành công", message);
            })
            .catch((error) => {
                console.log("Lỗi khi cập nhật trạng thái ", error);
                createToast("error", "fa-solid fa-circle-exclamation", "Lỗi", "Cập nhật trạng thái thất bại!");
            });
    }
    function UpdateUI(nextStatus) {
        const steps = document.querySelectorAll("#progressbar-2 li");

        steps.forEach((step) => {
            step.classList.add("text-muted");
            step.classList.remove("active");
        });
        for (let i = 0; i < steps.length; i++) {
            const step = steps[i];
            const stepStatus = parseInt(step.getAttribute("data-status"));
            if (stepStatus <= nextStatus) {
                step.classList.add("active");
                step.classList.remove("text-muted");
            }
        }
    }
    // hàm chọn địa chỉ buộc dữ liệu lên form
    window.selectAddress = function (button) {
        // lấy tr chưa các td
        let row = button.closest("tr");
        let street = row.cells[3].textContent;
        let ward = row.cells[4].textContent;
        let district = row.cells[5].textContent;
        let city = row.cells[6].textContent;

        document.getElementById("floatingstreet").value = street;
        document.getElementById("floatingward").value = ward;
        document.getElementById("floatingdistrict").value = district;
        document.getElementById("floatingcity").value = city;
    };
    // hàm lưu địa chỉ
    window.submitAddress = function () {
        const addressData = {
            addressId: document.getElementById("inputAddressId").value,
            customerId: document.getElementById("inputCustomerId").value,
        };
        Swal.fire({
            title: "Bạn có chắc chắn muốn thay đổi Địa chỉ không?",
            text: "Địa chỉ sẽ được cập nhật",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Xác nhận",
            cancelButtonText: "Hủy",
        }).then((result) => {
            if (result.isConfirmed) {
                fetch("/api/admin/orders/submitAddress", {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(addressData),
                })
                    .then((response) => {
                        if (!response.ok) {
                            throw new Error("Có lỗi xảy ra khi gửi dữ liệu.");
                        }
                        return response.text(); // Bạn có thể dùng response.text() nếu chỉ cần thông báo
                    })
                    .then((message) => {
                        // In ra thông báo từ server
                        createToast("success", "fa-solid fa-circle-check", "Thành công", message);
                        setTimeout(() => {
                            window.location.href = `/admin/orders/detail/${orderId}`;
                        }, 3500);
                    })
                    .catch((err) => {
                        createToast("error", "fa-solid fa-circle-exclamation", "Lỗi", "Cập nhật địa chỉ thất bại!");
                        console.error("Lỗi:", err);
                    });
            }
        });
    };
    // Hàm thêm mới địa chỉ trong modal
    window.addAddress = function () {
        let streetInput = document.getElementById("street");
        let cityInput = document.getElementById("city");
        let districtInput = document.getElementById("district");
        let wardInput = document.getElementById("ward");
        let inputCustomerId = document.getElementById("inputCustomerId");
        let addressData = {
            street: streetInput.value,
            city: cityInput.value,
            district: districtInput.value,
            ward: wardInput.value,
            customerId: inputCustomerId.value,
        };
        if (streetInput && cityInput && districtInput && wardInput && inputCustomerId) {
            // Thực hiện gọi API fetch
            fetch("/api/admin/orders/addAddress", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(addressData),
            })
                .then((response) => response.json())
                .then((address) => {
                    console.log(address);
                    const listAddressTable = document.querySelector(".listAddress");
                    listAddressTable.innerHTML = "";
                    let addressRows = "";
                    address.forEach((address, index) => {
                        addressRows += `
                            <tr>
                                <td>${index + 1}</td>
                                <td>${address.customer.fullname}</td>
                                <td>${address.customer.phoneNumber}</td>
                                <td>${address.street}</td>
                                <td>${address.ward}</td>
                                <td>${address.district}</td>
                                <td>${address.city}</td>
                                <td>
                                    <a href="javascript:void(0);" class="btn btn-outline-warning" onclick="selectAddress(this)" data-bs-dismiss="modal">Chọn</a>
                                </td>
                                 <input
                                                        type="hidden"
                                                        th:value="${address.addressId}"
                                                        id="inputAddressId"
                                                    />
                                                    <input
                                                        type="hidden"
                                                        th:value="${address.customer.customerId}"
                                                        id="inputCustomerId"
                                                    />
                            </tr>`;
                    });
                    listAddressTable.innerHTML = addressRows;
                    // In ra thông báo từ server
                    createToast("success", "fa-solid fa-circle-check", "Thành công", "Thêm mới địa chỉ thành công");
                })
                .catch((error) => {
                    console.error("Error:", error);
                    createToast("error", "fa-solid fa-circle-exclamation", "Lỗi", "Thêm mới địa chỉ thất bại!");
                });
        } else {
            console.log("Không có dữ liệu!");
        }
    };
    // cập nhật dữ liệu sau khi thêm mới 1 địa chỉ
    function updateAddressList(address) {
        const tbody = document.querySelector("#modalIdSelectAddress tbody");
        const newRow = document.createElement("tr");
        newRow.innerHTML = `
        <td>${tbody.children.length + 1}</td>
        <td>${address.customer.fullname}</td>
        <td>${address.customer.phoneNumber}</td>
        <td>${address.street}</td>
        <td>${address.ward}</td>
        <td>${address.district}</td>
        <td>${address.city}</td>
        <td>
       <a
        href="javascript:void(0);"
        class="btn btn-outline-warning"
        onclick="selectAddress(this)"
        data-bs-dismiss="modal"
        id="selectAddress"
        >
        Chọn
       </a>
       </td>
        <input
        type="hidden"
        th:value="${address.addressId}"
        id="inputAddressId"
        />
        <input
        type="hidden"
        th:value="${address.customer.customerId}"
        id="inputCustomerId"
        />
        `;
        // thêm hàng mới vào bảng
        tbody.appendChild(newRow);
    }
    // hàm buộc dự liệu địa chỉ mặc định lên form
    window.setDefaultAddress = function () {
        let addressText = document.getElementById("setDefaultAddress").textContent;

        let addressSplitArray = addressText.split(",").map((arr) => arr.trim());
        let street = addressSplitArray[0];
        let ward = addressSplitArray[1];
        let district = addressSplitArray[2];
        let city = addressSplitArray[3];

        document.getElementById("floatingstreet").value = street;
        document.getElementById("floatingward").value = ward;
        document.getElementById("floatingdistrict").value = district;
        document.getElementById("floatingcity").value = city;
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

    const mainModal = new bootstrap.Modal(document.getElementById("modalIdUpdateOrder"));
    const selectAddressModal = new bootstrap.Modal(document.getElementById("modalIdSelectAddress"));
    const addAddressModal = new bootstrap.Modal(document.getElementById("modalIdAddAddress"));

    document.getElementById("modalIdSelectAddress").addEventListener("shown.bs.modal", () => {
        mainModal.hide();
    });
    document.getElementById("modalIdAddAddress").addEventListener("shown.bs.modal", () => {
        mainModal.hide();
        selectAddressModal.show();
    });
    document.getElementById("modalIdAddAddress").addEventListener("hidden.bs.modal", () => {
        selectAddressModal.show();
    });
    document.getElementById("modalIdSelectAddress").addEventListener("hidden.bs.modal", () => {
        mainModal.show();
    });
});
