import { format } from "https://cdn.skypack.dev/date-fns";

window.addEventListener("DOMContentLoaded", () => {
    const orderElement = document.getElementById("orderId");
    const orderId = orderElement.textContent;
    const steps = document.querySelectorAll("#progressbar-2 li");
    let currentStatus = 1;
    let nextStatus = 5; // mặc định 5 để không lỗi
    const statuses = [
        { id: 1, text: "Xác nhận đang đóng gói" },
        { id: 2, text: "Xác nhận đã giao cho vận chuyển" },
        { id: 3, text: "Xác nhận đang giao hàng" },
        { id: 4, text: "Xác nhận đã thanh toán" },
        { id: 5, text: "Xác nhận hoàn thành" },
    ];
    const btnConfirm = document.getElementById("btnConfirm");
    const btnPreviousStatus = document.getElementById("btnPreviousStatus");
    const buttonPayment = document.getElementById("confirmPayment");
    if (btnConfirm) {
        btnConfirm.addEventListener("click", function (e) {
            e.preventDefault();
            steps.forEach((step) => {
                if (step.classList.contains("active")) {
                    currentStatus = parseInt(step.getAttribute("data-status"));
                }
            });
            if (currentStatus < steps.length) {
                nextStatus = currentStatus + 1;
                if (currentStatus === 4) {
                    openModalPayment();
                    const modal = new bootstrap.Modal(document.getElementById("confirmPaymentModal"));
                    modal.show();
                } else {
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
                            UpdateUI(nextStatus); // cập nhật giao diện
                            // thay đổi text button
                            if (nextStatus < statuses.length + 1) {
                                btnConfirm.textContent = statuses[nextStatus - 1].text + " ";
                                const icon = document.createElement("i");
                                icon.className = "fas fa-arrow-right";
                                btnConfirm.appendChild(icon);
                            } else {
                                btnConfirm.textContent = "Hoàn thành";
                            }
                        }
                    });
                }
            }
        });
    }

    if (btnPreviousStatus) {
        btnPreviousStatus.addEventListener("click", () => {
            let currentStatus = 1;
            steps.forEach((step) => {
                if (step.classList.contains("active")) {
                    currentStatus = parseInt(step.getAttribute("data-status"));
                }
            });
            if (currentStatus == 5) {
                btnPreviousStatus.setAttribute("disabled", "true");
            }
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
                        // XỬ lý ẩn thời gian khi quay lại trạng thái trước
                        let statusElementClass;
                        switch (currentStatus) {
                            case 2:
                                statusElementClass = "packing";
                                break;
                            case 3:
                                statusElementClass = "shipped";
                                break;
                            case 4:
                                statusElementClass = "inTransit";
                                break;
                            case 5:
                                statusElementClass = "paid";
                                break;
                            case 6:
                                statusElementClass = "completed";
                                break;
                            default:
                                break;
                        }
                        // lấy phẩn tử thời gian
                        const statusTimeElement = document.querySelector(`.${statusElementClass}`);
                        if (statusTimeElement) {
                            statusTimeElement.classList.add("timeUpdatedStatus");
                            statusTimeElement.textContent = "dd-MM-yyyy HH:mm:ss";
                        }
                        // gọi hàm cập nhật trạng thái
                        updateStatus(orderId, previousStatus);
                        UpdateUI(previousStatus); // cập nhật giao diện
                        if (previousStatus < statuses.length) {
                            btnConfirm.textContent = statuses[previousStatus - 1].text + " ";
                            const icon = document.createElement("i");
                            icon.className = "fas fa-arrow-right";
                            btnConfirm.appendChild(icon);
                        } else {
                            btnConfirm.textContent = statuses[statuses.length - 1].text + " ";
                            const icon = document.createElement("i");
                            icon.className = "fas fa-arrow-right";
                            btnConfirm.appendChild(icon);
                        }
                    }
                });
            }
        });
    }
    const btnCancel = document.getElementById("btnCancel");
    if (btnCancel) {
        btnCancel.addEventListener("click", () => {
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
                    // cập nhật hình ảnh
                    const cardStatusElement = document.getElementById("cardStatus");
                    cardStatusElement.innerHTML = "";
                    const newStatusElement = document.createElement("div");
                    newStatusElement.innerHTML = `<div class="order-status text-danger text-center">
              <i class="bi bi-x-circle-fill"></i>
              <p >Đơn hàng ${orderId} đã bị hủy</p>
              <p class="canceled"></p>
              </div>`;
                    cardStatusElement.appendChild(newStatusElement);
                    // gọi hàm cập nhật trạng thái
                    updateStatus(orderId, cancelStatus);
                }
            });
        });
    }

    function updateStatus(orderId, status) {
        fetch("/api/admin/orders/update-status", {
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
                const { statusTime, statusText, statusDesciption, username } = data;
                let newStatusElementId;
                switch (statusText) {
                    case "":
                        console.log("Không có trạng thái");
                        break;
                    case "PACKING":
                        newStatusElementId = "packing";
                        break;
                    case "SHIPPED":
                        newStatusElementId = "shipped";
                        break;
                    case "IN_TRANSIT":
                        newStatusElementId = "inTransit";
                        break;
                    case "PAID":
                        newStatusElementId = "paid";
                        break;
                    case "COMPLETED":
                        newStatusElementId = "completed";
                        break;
                    case "CANCELED":
                        newStatusElementId = "canceled";
                        break;
                    default:
                        console.log("Status không xác định hoặc chưa xử lý!");
                        break;
                }
                if (newStatusElementId) {
                    const statusElement = document.querySelector(`.${newStatusElementId}`);
                    if (statusElement) {
                        statusElement.classList.remove("timeUpdatedStatus"); // xóa class để hiển thị thời gian
                        statusElement.innerHTML = statusTime
                            ? format(new Date(statusTime), "dd-MM-yyyy HH:mm:ss")
                            : "dd-MM-yyyy HH:mm:ss";
                    }
                }
                // hiển thị trạng thái đơn hàng
                if (statusDesciption) {
                    const statusDesciptionElement = document.getElementById("statusDesciption");
                    statusDesciptionElement.textContent = statusDesciption;
                }
                // hiển thị thời gian trong lịch sử hóa Đơn
                const statusHistoryElement = document.getElementById("statusHistory");
                const tr = document.createElement("tr");
                const statusTimeFormatted = statusTime ? format(new Date(statusTime), "dd-MM-yyyy HH:mm:ss") : "";
                tr.innerHTML = `
                    <td>${username}</td>
                    <td>${statusTimeFormatted}</td>
                    <td>${statusDesciption}</td>
                `;
                // Thêm vào đầu danh sách
                if (statusHistoryElement.children.length > 0) {
                    // Nếu có dòng con, chèn vào đầu
                    statusHistoryElement.insertBefore(tr, statusHistoryElement.firstElementChild);
                } else {
                    // Nếu bảng trống, thêm vào cuối
                    statusHistoryElement.appendChild(tr);
                }
                createToast("success", "fa-solid fa-circle-check", "thành công", "Cập nhật trạng thái thành công");
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
        Swal.fire({
            title: `Bạn muốn In hóa đơn?`,
            text: "",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Xác nhận",
            cancelButtonText: "Hủy",
        }).then((result) => {
            if (result.isConfirmed) {
                let printWindow = window.open(`/api/invoices/generate?orderId=${orderId}`, "_blank");
                printWindow.onload = function () {
                    printWindow.print();
                };
            }
        });
    };
    // hàm chọn địa chỉ buộc dữ liệu lên form
    let addressIdInput; // biến lưu addressid mặc đinh và không mặc định
    const updateAddressButton = document.getElementById("updateAddress");
    window.selectAddress = function (button) {
        // lấy tr chứa các td
        let row = button.closest("tr"); // tìm phần tử cha gần nhất của nút
        let street = row.cells[3].textContent;
        let ward = row.cells[4].textContent;
        let district = row.cells[5].textContent;
        let city = row.cells[6].textContent;
        const addressIdElement = row.querySelector(".inputAddressId");
        if (addressIdElement) {
            addressIdInput = addressIdElement.value;
        }
        const floatingstreet = document.getElementById("floatingstreet");
        const floatingward = document.getElementById("floatingward");
        const floatingdistrict = document.getElementById("floatingdistrict");
        const floatingcity = document.getElementById("floatingcity");
        if (floatingstreet && floatingward && floatingdistrict && floatingcity) {
            floatingstreet.value = street;
            floatingward.value = ward;
            floatingdistrict.value = district;
            floatingcity.value = city;
        } else {
            document.getElementById("floatingstreetNotDefault").value = street; // đang lỗi
            document.getElementById("floatingcityNotDefault").value = city;
            document.getElementById("floatingdistrictNotDefault").value = district;
            document.getElementById("floatingwardNotDefault").value = ward;
        }
        // hiện button cập nhật địa chỉ bên bán hàng tại quầy
        if (updateAddressButton) {
            updateAddressButton.style.visibility = "visible";
        }
    };
    // hàm lưu địa chỉ
    const fullnameElement = document.getElementById("fullname");
    const phoneNumberElement = document.getElementById("phoneNumber");
    const setDefaultAddressElement = document.getElementById("setDefaultAddress");
    const setNotDefaultAddressElement = document.getElementById("setNotDefaultAddress");
    window.submitAddress = function () {
        const addressData = {
            addressId: addressIdInput,
            customerId: document.getElementById("inputCustomerId").value,
            customer: {
                fullname: document.getElementById("floatinghovaten").value.trim(),
                phoneNumber: document.getElementById("floatingsodienthoai").value.trim(),
            },
        };
        Swal.fire({
            title: "Xác nhận thay đổi thông tin khách hàng?",
            text: `Thông tin khách hàng ${addressData.customer.fullname} sẽ được cập nhật`,
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
                        return response.json(); // Bạn có thể dùng response.text() nếu chỉ cần thông báo
                    })
                    .then((data) => {
                        const { fullname, phoneNumber, street, ward, district, city } = data;
                        if (fullnameElement && phoneNumberElement) {
                            fullnameElement.textContent = fullname;
                            phoneNumberElement.textContent = phoneNumber;
                        }
                        let spanAddressElement = `${street}, ${ward}, ${district}, ${city}`;
                        if (setDefaultAddressElement) {
                            setDefaultAddressElement.textContent = spanAddressElement;
                        }
                        if (setNotDefaultAddressElement) {
                            setNotDefaultAddressElement.textContent = spanAddressElement;
                        }
                        if (updateAddressButton) {
                            // ẩn nút cập nhật khi cập nhật xong
                            updateAddressButton.style.visibility = "hidden";
                        }
                        // In ra thông báo từ server
                        createToast(
                            "success",
                            "fa-solid fa-circle-check",
                            "Thành công",
                            "Cập nhật thông tin khách hàng thành công"
                        );
                    })
                    .catch((err) => {
                        createToast(
                            "error",
                            "fa-solid fa-circle-exclamation",
                            "Lỗi",
                            "Cập nhật thông tin khách hàng thất bại!"
                        );
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
    // khi nào chọn phường thì mới cho thêm địa chỉ
    const buttonNewAddress = document.getElementById("newAddresses");
    // Lắng nghe sự kiện change cho select city
    document.getElementById("ward").addEventListener("change", function () {
        const selectectElement = document.getElementById("ward"); // Lấy giá trị thành phố đã chọn
        const selectedOption = selectectElement.options[selectectElement.selectedIndex];
        selectedWard = selectedOption.textContent; // Lấy giá trị thành phố đã chọn
        buttonNewAddress.removeAttribute("disabled");
    });
    // Hàm thêm mới địa chỉ trong modal
    buttonNewAddress.setAttribute("disabled", true);
    window.addAddress = function () {
        let streetInput = document.getElementById("street");
        let inputCustomerId = document.getElementById("customerId");
        if (streetInput && streetInput.value.trim() === "") {
            createToast("warning", "fa-solid fa-triangle-exclamation", "Cảnh báo", "Vui lòng nhập địa chỉ cụ thể!");
            return;
        }
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
                        createToast(
                            "error",
                            "fa-solid fa-circle-exclamation",
                            "Lỗi",
                            "Khách hàng chỉ thêm được tối đa 3 địa chỉ!"
                        );
                        return;
                    }
                    return response.json();
                })
                .then((addresses) => {
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
                                    <a href="javascript:void(0);" class="btn btn-outline-warning" onclick="selectAddress(this)" data-bs-toggle="modal"
                                                            data-bs-target="#modalIdUpdateOrder"
                                                            id="selectAddress">Chọn</a>
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
        const defaultAddress = document.getElementById("setDefaultAddress");
        if (defaultAddress) {
            let addressText = defaultAddress.textContent;

            let addressSplitArray = addressText.split(",").map((arr) => arr.trim());
            let street = addressSplitArray[0];
            let ward = addressSplitArray[1];
            let district = addressSplitArray[2];
            let city = addressSplitArray[3];

            document.getElementById("floatingstreet").value = street;
            document.getElementById("floatingward").value = ward;
            document.getElementById("floatingdistrict").value = district;
            document.getElementById("floatingcity").value = city;
        }
    };
    // hàm lấy kiểu phương thức thanh toán
    let transactionType;
    function updateTransactionType() {
        if (document.getElementById("bankTransfer").checked) {
            transactionType = "BANK_TRANSFER";
        } else if (document.getElementById("cash").checked) {
            transactionType = "COD";
        }
    }
    document.querySelectorAll("input[name='paymentMethod']").forEach((input) => {
        input.addEventListener("change", function () {
            updateTransactionType();
        });
    });
    // gọi hàm lần đầu khi trang được load để lấy kiểu thanh toán
    updateTransactionType();
    // hàm thanh toán
    window.handlePayment = function () {
        if (nextStatus === 5) {
            btnPreviousStatus.setAttribute("disabled", true);
        }
        // thay đổi text button
        if (nextStatus < statuses.length + 1) {
            btnConfirm.textContent = statuses[nextStatus - 1].text + " ";
            const icon = document.createElement("i");
            icon.className = "fas fa-arrow-right";
            btnConfirm.appendChild(icon);
        } else {
            btnConfirm.textContent = "Hoàn thành";
        }
        const data = {
            transactionType: transactionType,
            orderId: orderId,
        };
        fetch("/api/admin/orders/payment", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Có lỗi xảy ra khi gửi dữ liệu.");
                }
                return response.json();
            })
            .then((order) => {
                const data = {
                    totalAmount: order.totalAmount, // chưa format
                    transactionTime: order.transactionTime,
                    transactionType: order.transactionType,
                    paymentMethod: order.paymentMethod,
                    transactionStatus: order.transactionStatus,
                };
                // format thời gian thanh toán
                const formattedDate = format(new Date(data.transactionTime), "dd-MM-yyyy HH:mm:ss");
                // format tổng tiền thanh toán
                const formatedTotalAmount = `${data.totalAmount.toLocaleString("vi-VN")}.000 VND`;
                const parentTablePayment = document.querySelector(".parentTablePayment");
                const tablePayment = document.querySelector(".tablePayment");
                const imageNoDataPayment = document.querySelector(".imageNoDataPayment");
                const buttonOpenModalPayment = document.getElementById("buttonOpenModalPayment");
                tablePayment.innerHTML = "";
                let row = `<tr>
                                                <td>${formatedTotalAmount}</td>
                                                <td>${formattedDate}</td>
                                                <td>${data.transactionType}</td>
                                                <td>${data.paymentMethod}</td>
                                                <td>${data.transactionStatus}</td>
                            </tr>`;
                tablePayment.innerHTML = row;
                if (parentTablePayment && imageNoDataPayment) {
                    parentTablePayment.style.display = "block"; // hiện table
                    imageNoDataPayment.style.display = "none"; // ẩn hình ảnh
                }
                if (buttonOpenModalPayment) {
                    buttonOpenModalPayment.style.display = "none"; // ẩn button thanh toán
                }
                // cập nhật trong server
                updateStatus(orderId, 5);
                // câp nhật giao diện trạng thái
                UpdateUI(5); // 5 là trạng thái thanh toán
                // In ra thông báo từ server
                createToast("success", "fa-solid fa-circle-check", "Thành công", "Thanh toán thành công");
            })
            .catch((err) => {
                createToast("error", "fa-solid fa-circle-exclamation", "Lỗi", "Thanh toán thất bại!");
                console.error("Lỗi:", err);
            });
    };
    // vô hiệu hóa nút thanh toán
    window.openModalPayment = function () {
        if (buttonPayment) {
            buttonPayment.setAttribute("disabled", true);
        }
    };
    window.deleteTextInputCustomerGive = function () {
        document.getElementById("customerGive").value = "";
        document.getElementById("tienthua").value = "0 VND";
    };
    // hàm tính toán tiền khách đưa
    document.getElementById("customerGive").addEventListener("input", function () {
        let voucherTotalSum = document.getElementById("voucherTotalSum").value.trim();
        console.log(voucherTotalSum);

        // Xóa VND nếu có và định dạng lại giá trị
        let voucherTotalSumMoney = voucherTotalSum.replace("VND", "").replace(/\./g, "");
        let voucherTotalSumMoneyFormat = parseInt(voucherTotalSumMoney, 10); // Chuyển đổi thành số nguyên
        console.log(voucherTotalSumMoneyFormat);

        const customerGive = document.getElementById("customerGive").value.trim();
        const tienthua = document.getElementById("tienthua");

        // Xóa dấu chấm khi nhập vào và tính toán số tiền đã đưa
        let customerGiveFormatted = customerGive.replace(/\./g, ""); // Loại bỏ dấu chấm
        let customerGiveMoney = parseInt(customerGiveFormatted, 10); // Chuyển thành số
        console.log("Tiền khách đưa ", customerGiveMoney);
        // Thêm dấu chấm vào số tiền đã nhập
        let customerGiveWithComma = customerGiveFormatted.replace(/\B(?=(\d{3})+(?!\d))/g, ".");

        // Cập nhật lại giá trị ô input tiền khách đưa
        document.getElementById("customerGive").value = customerGiveWithComma;

        // Tính số tiền thừa và thêm dấu .
        let tienthuaValue = customerGiveMoney - voucherTotalSumMoneyFormat;
        tienthua.value = tienthuaValue >= 0 ? tienthuaValue.toLocaleString() + " VND" : "0 VND";

        if (customerGiveMoney >= voucherTotalSumMoneyFormat) {
            if (buttonPayment) {
                buttonPayment.removeAttribute("disabled");
            }
        } else {
            if (buttonPayment) {
                buttonPayment.setAttribute("disabled", true);
            }
        }
    });
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
