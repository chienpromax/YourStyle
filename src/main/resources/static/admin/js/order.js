window.addEventListener("DOMContentLoaded", () => {
    const orderElement = document.getElementById("orderId");
    const orderId = orderElement.textContent;
    const steps = document.querySelectorAll("#progressbar-2 li");
    let currentStatus = 1;
    const statuses = [
        { id: 1, text: "Xác nhận đang đóng gói" },
        { id: 2, text: "Xác nhận đã giao cho vận chuyển" },
        { id: 3, text: "Xác nhận đang giao hàng" },
        { id: 4, text: "Xác nhận hoàn thành" },
        { id: 5, text: "Xác nhận trả hàng" },
    ];
    document.getElementById("btnConfirm").addEventListener("click", function (e) {
        e.preventDefault();
        steps.forEach((step) => {
            if (step.classList.contains("active")) {
                currentStatus = parseInt(step.getAttribute("data-status"));
            }
        });
        if (currentStatus < steps.length) {
            const nextStatus = currentStatus + 1;
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
                    UpdateUI(nextStatus); // cập nhật giao diện
                    // gọi hàm cập nhật trạng thái
                    updateStatus(orderId, nextStatus);
                }
            });
        }
        // this sẽ tham chiếu đến nút xác nhận nếu mình click vào
        if (currentStatus < statuses.length) {
            this.textContent = statuses[currentStatus - 1].text + " ";
            const icon = document.createElement("i");
            icon.className = "fas fa-arrow-right";
            this.appendChild(icon);
        } else {
            this.textContent = statuses[statuses.length - 1].text + " ";
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
                    UpdateUI(previousStatus); // cập nhật giao diện
                    // gọi hàm cập nhật trạng thái
                    updateStatus(orderId, previousStatus);
                }
            });
        }
    });
    document.getElementById("btnCancel").addEventListener("click", () => {
        const cancelStatus = 0;
        Swal.fire({
            title: `Bạn có chắc chắn muốn hủy đơn hàng ${orderId}?`,
            text: `Trạng thái đơn hàng ${orderId} sẽ được cập nhật`,
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
                    return reponse.json();
                } else {
                    throw new Error("Cập nhật trạng thái thất bại!");
                }
            })
            .then((data) => {
                const { orderId } = data;
                createToast("success", "fa-solid fa-circle-check", "thành công", "Cập nhật trạng thái thành công");
                setTimeout(() => {
                    window.location.href = `/admin/orders/detail/${orderId}`;
                }, 1000);
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
    window.downloadInvoice = function (orderId) {
        window.open(`/api/invoices/generate?orderId=${orderId}`, "_blank");
    };
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
    let selectedCity = "";
    let selectedDistrict = "";
    let selectedWard = "";
    // Lắng nghe sự kiện change cho select city
    document.getElementById("city").addEventListener("change", function () {
        const selectectElement = document.getElementById("city"); // Lấy giá trị thành phố đã chọn
        const selectedOption = selectectElement.options[selectectElement.selectedIndex];
        selectedCity = selectedOption.textContent;
    });
    // Lắng nghe sự kiện change cho select city
    document.getElementById("district").addEventListener("change", function () {
        const selectectElement = document.getElementById("district"); // Lấy giá trị thành phố đã chọn
        const selectedOption = selectectElement.options[selectectElement.selectedIndex];
        selectedDistrict = selectedOption.textContent; // Lấy giá trị thành phố đã chọn
    });
    // Lắng nghe sự kiện change cho select city
    document.getElementById("ward").addEventListener("change", function () {
        const selectectElement = document.getElementById("ward"); // Lấy giá trị thành phố đã chọn
        const selectedOption = selectectElement.options[selectectElement.selectedIndex];
        selectedWard = selectedOption.textContent; // Lấy giá trị thành phố đã chọn
    });
    // Hàm thêm mới địa chỉ trong modal
    window.addAddress = function () {
        let streetInput = document.getElementById("street");
        let inputCustomerId = document.getElementById("customerId");

        let addressData = {
            street: streetInput.value,
            city: selectedCity.trim(),
            district: selectedDistrict.trim(),
            ward: selectedWard.trim(),
            customerId: inputCustomerId.value,
        };
        if (streetInput && inputCustomerId) {
            // Thực hiện gọi API fetch
            fetch("/api/admin/orders/addAddress", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(addressData),
            })
                .then((response) => {
                    if (!response.ok) {
                        throw new Error("Network response was not ok");
                    }
                    return response.json();
                })
                .then((addresses) => {
                    console.log(addresses);
                    const filteredData = addresses.map((address) => ({
                        // trả về đối tượng phải có (
                        addressId: address.addressId,
                        street: address.street,
                        ward: address.ward,
                        district: address.district,
                        city: address.city,
                        customer: {
                            customerId: address.customer.customerId,
                            fullname: address.customer.fullname,
                            phoneNumber: address.customer.phoneNumber,
                        },
                    }));

                    const listAddressTable = document.querySelector(".listAddress");
                    listAddressTable.innerHTML = "";
                    let addressRows = "";

                    filteredData.forEach((address, index) => {
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
});
