import { format } from "https://cdn.skypack.dev/date-fns";
window.addEventListener("DOMContentLoaded", function () {
    const orderId = document.getElementById("orderId").value;
    const printInvoice = document.getElementById("printInvoice");
    if (printInvoice) {
        printInvoice.addEventListener("click", function () {
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
                    const printWindow = window.open(`/api/invoices/generate-instore?orderId=${orderId}`);
                    // Mở hộp thoại in trên cửa sổ mới
                    printWindow.onload = function () {
                        printWindow.print();
                    };
                }
            });
        });
    }
    // gán lại sự kiện click cho button khi trả API về
    function rebindQuantityButtons() {
        // Lắng nghe sự kiện click vào nút giảm số lượng
        document.querySelectorAll(".decreaseQuantity").forEach(function (button) {
            button.addEventListener("click", function () {
                handleQuantityChange(this, "decrease");
            });
        });

        // Lắng nghe sự kiện click vào nút tăng số lượng
        document.querySelectorAll(".increaseQuantity").forEach(function (button) {
            button.addEventListener("click", function () {
                handleQuantityChange(this, "increase");
            });
        });
    }
    function handleQuantityChange(button, action) {
        let index = button.getAttribute("data-index");
        if (index) {
            // Tìm ô input tương ứng với data-index
            let quantityInput = document.querySelector(`.quantityInput[data-index="${index}"]`);

            if (quantityInput) {
                // Kiểm tra nếu phần tử tồn tại
                let currentValue = parseInt(quantityInput.value);
                if (action === "decrease" && currentValue > 1) {
                    quantityInput.value = currentValue - 1;
                } else if (action === "increase") {
                    quantityInput.value = currentValue + 1;
                }

                const rows = button.closest("tr");
                const productVariantId = rows.cells[1].querySelector('input[name="productVariantId"]').value;
                const colorId = rows.cells[2].querySelector('input[name="colorId"]').value;
                const sizeId = rows.cells[3].querySelector('input[name="sizeId"]').value;
                const orderDetailData = {
                    orderId: orderId,
                    productVariantId: productVariantId,
                    colorId: colorId,
                    sizeId: sizeId,
                };
                fetch(`/api/admin/sell/${action}Quantity`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(orderDetailData),
                })
                    .then((reponse) => {
                        if (!reponse.ok) {
                            throw new Error("Lỗi server không trả dữ liệu về!");
                        }
                        return reponse.json();
                    })
                    .then((data) => {
                        updateOrderDetailTable(data);
                        rebindQuantityButtons();
                        createToast(
                            "success",
                            "fa-solid fa-circle-check",
                            "Thành công",
                            `${action === "increase" ? "Tăng" : "Giảm"} số lượng sản phẩm thành công`
                        );
                    })
                    .catch((err) => {
                        console.error("Lỗi: " + err);
                        createToast(
                            "error",
                            "fa-solid fa-circle-exclamation",
                            "Lỗi",
                            `${action === "increase" ? "Tăng" : "Giảm"} số lượng sản phẩm thất bại!`
                        );
                    });
            } else {
                console.error(`Không tìm thấy ô input với data-index="${index}"`);
            }
        }
    }

    window.deleteProduct = function (orderDetailId) {
        Swal.fire({
            title: "Bạn có chắc chắn muốn xóa sản phẩm này không?",
            text: "sản phẩm này sẽ được xóa ra khỏi đơn",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Xác nhận",
            cancelButtonText: "Hủy",
        }).then((result) => {
            if (result.isConfirmed) {
                fetch(`/api/admin/sell/deleteProduct/${orderDetailId}/${orderId}`, {
                    method: "DELETE",
                    headers: {
                        "Content-Type": "application/json",
                    },
                })
                    .then((response) => {
                        if (!response.ok) {
                            if (response.status === 404) {
                                throw new Error("Sản phẩm không tồn tại hoặc đã bị xóa");
                            } else {
                                throw new Error("Lỗi server không trả dữ liệu về");
                            }
                        }
                        return response.json();
                    })
                    .then((data) => {
                        updateOrderDetailTable(data);
                        rebindQuantityButtons();
                        createToast(
                            "success",
                            "fa-solid fa-circle-check",
                            "Thành công",
                            "Xóa sản phẩm trong đơn thành công"
                        );
                    })
                    .catch((error) => {
                        createToast(
                            "error",
                            "fa-solid fa-circle-exclamation",
                            "Lỗi",
                            "Xóa sản phẩm trong đơn thất bại!"
                        );
                        console.log("có lỗi xảy ra: " + error);
                    });
            }
        });
    };
    // Nút chọn
    let colorId;
    let sizeId;
    let productVariantId;
    let price;
    window.openChildModal = function (button) {
        const rows = button.closest("tr"); // Nút chọn được nhấn
        colorId = rows.querySelector(".colorId").value;
        sizeId = rows.querySelector(".sizeId").value;
        productVariantId = rows.querySelector(".productVariantId").value;

        const productName = rows.cells[2].textContent;
        const categoryName = rows.cells[3].textContent;
        const color = rows.cells[4].textContent;
        const size = rows.cells[5].textContent;
        const quantity = rows.cells[7].textContent;
        // Lấy các phần tử span theo điều kiện có hoặc không có discount
        const oldPriceElement = rows.querySelector("span:not([class])"); // span không có class, dùng cho oldPrice
        const discountedPriceElement = rows.querySelector("span.text-danger.fw-bold"); // span dùng cho discountedPrice
        const lineThroughElement = rows.querySelector("span.text-decoration-line-through.d-block"); // span dùng cho oldPrice khi có discount
        let oldPrice = lineThroughElement ? lineThroughElement.textContent.trim() : "N/A";
        let discountedPrice = discountedPriceElement ? discountedPriceElement.textContent.trim() : "N/A";
        let oldPriceNotDiscount = oldPriceElement ? oldPriceElement.textContent.trim() : "N/A";
        // Xác định giá nào sẽ hiển thị
        let modalTitleContent = `<h5>${productName} "${color}"</h5>`;
        let modalBodyContent;
        if (discountedPriceElement) {
            // Nếu có giảm giá
            price = parseFloat(discountedPrice.replace(" VND", "").replace(",", ""));
            price = price.toFixed(2); // dùng để thêm sản phẩm
            modalBodyContent = `
                    <p><strong>Danh mục:</strong> ${categoryName}</p>
                    <p>
                        <strong>Giá:</strong> <span class="text-decoration-line-through">${oldPrice}   </span>
                        <span class="text-danger fw-bold"> ${discountedPrice}</span>
                    </p>
                    <p><strong>Size:</strong> ${size}</p>
                    <p><strong class="text-danger">Số lượng: ${quantity}</strong>
                    <input class="" type="number" value="1" min="0" name="inputQuantity" id="inputQuantity"/></p>`;
        } else {
            // Nếu không có giảm giá
            price = parseFloat(oldPriceNotDiscount.replace(" VND", "").replace(",", ""));
            price = price.toFixed(2); // dùng để thêm sản phẩm

            modalBodyContent = `
                    <p><strong>Danh mục:</strong> ${categoryName}</p>
                    <p>
                        <strong>Giá:</strong> <span class="text-danger fw-bold">${oldPriceNotDiscount}</span>
                    </p>
                    <p><strong>Size:</strong> ${size}</p>
                    <p><strong class="text-danger">Số lượng: ${quantity}</strong>
                    <input type="number" value="1" min="0" name="inputQuantity" id="inputQuantity"/></p>`;
        }
        selectProductModal.querySelector(".modal-title").innerHTML = modalTitleContent;
        selectProductModal.querySelector(".modal-body").innerHTML = modalBodyContent;
    };
    // Id của modal
    // thêm sản phẩm
    const selectProductModal = document.getElementById("selectProductModal");
    window.confirmInsertProduct = function () {
        const quantity = document.getElementById("inputQuantity").value;
        // console.log("productVariantId " + productVariantId);
        // console.log("orderId " + orderId);
        // console.log("sizeId " + sizeId);
        // console.log("colorId " + colorId);
        // console.log("quantity " + quantity);
        // console.log("price " + price);
        const orderDetailData = {
            orderId: orderId,
            productVariantId: productVariantId,
            discountPrice: price,
            quantity: quantity,
            sizeId: sizeId,
            colorId: colorId,
        };
        fetch("/api/admin/sell/addProduct", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(orderDetailData),
        })
            .then((reponse) => {
                if (!reponse.ok) {
                    throw new Error("Lỗi server không trả dữ liệu về!");
                }
                return reponse.json();
            })
            .then((data) => {
                updateOrderDetailTable(data);
                rebindQuantityButtons();
                createToast("success", "fa-solid fa-circle-check", "Thành công", "Thêm sản phẩm vào đơn thành công");
            })
            .catch((err) => {
                console.error("Lỗi: " + err);
                createToast("error", "fa-solid fa-circle-exclamation", "Lỗi", "Thêm sản phẩm vào đơn thất bại!");
            });
    };
    const confirmButton = document.getElementById("confirmInsertProduct");
    if (confirmButton) {
        confirmButton.addEventListener("click", confirmInsertProduct);
    }
    // tổng tiền sau khi giảm voucher nếu có
    const totalAmountFinalElement = document.getElementById("totalAmountFinal");
    const tienThieuElement = document.getElementById("tienThieu"); // tiền thiếu
    // tổng tiền ở trong modal thanh toán
    const TotalAmountELement = document.getElementById("TotalAmount");
    // gọi hàm lắng nghe sự kiện nút tăng và giảm
    rebindQuantityButtons();
    function updateOrderDetailTable(data) {
        const totalElement = document.getElementById("totalSum"); // lấy element tổng tiền
        const goodMoneyElement = document.getElementById("goodMoney"); // tiền hàng
        const voucherCodeElement = document.querySelector(".voucherCode");
        const percentVoucherElement = document.querySelector(".percentVoucher"); // phần trăm
        const discountVoucherElement = document.querySelector(".discountVoucher"); // vnd
        const tbody = document.querySelector(".dsOrderDetail"); // Chọn tbody
        tbody.innerHTML = ""; // Xóa nội dung cũ
        let rows = "";
        let formattedTotal;
        const { orderDetailDtos, voucherDto } = data;
        const { voucherCode, discountType, formattedDiscount, finalTotalAmount } = voucherDto; // VOUCHER
        if (voucherCodeElement) {
            voucherCodeElement.value = voucherCode;
        }
        if (discountType == 2) {
            // giảm phần trăm
            if (percentVoucherElement) {
                percentVoucherElement.value = formattedDiscount;
            }
        } else if (discountType == 1 || discountType == 3) {
            if (discountVoucherElement) {
                discountVoucherElement.textContent = formattedDiscount;
            }
        }
        orderDetailDtos.forEach((dto, index) => {
            const {
                productVariantId,
                colorId,
                sizeId,
                image,
                name,
                colorName,
                sizeName,
                oldPrice,
                newPrice,
                quantity,
                totalSum,
                formattedTotalAmount,
                discount,
                orderDetailId,
                discountPercent,
            } = dto;
            formattedTotal = formattedTotalAmount; // gán cho tổng tiền
            let discountBadge = "";
            let priceHTML = "";
            if (discount) {
                discountBadge = `<span class="badge bg-success badge-custom position-absolute top-0 right-0">${discountPercent}</span>`;
                priceHTML = `<span class="text-decoration-line-through d-block">${oldPrice}</span>
                             <span class="text-danger fw-bold">${newPrice}</span>`;
            } else {
                priceHTML = `<span>${oldPrice}</span>`;
            }
            rows += `
        <tr class="align-middle">
        <td scope="row" class="position-relative">
            <img src="/admin/img/${image}" alt="" class="img-fluid rounded-circle object-cover" style="width: 60px" />
            ${discountBadge}
        </td>
        <td>
            <input
                class="productVariantIdOrderDetail"
                type="hidden"
                name="productVariantId"
                value="${productVariantId}"
            />
            ${name}
        </td>
        <td>
            <input
            type="hidden"
            name="colorId"
            value="${colorId}"
            />
            ${colorName}
        </td>
        <td>
            <input
            type="hidden"
            name="sizeId"
            value="${sizeId}"
            />
            ${sizeName}
        </td>
        <td>${priceHTML}</td>
        <td>
                                                        <div class="input-group">
                                                            <button
                                                                class="btn btn-outline-secondary decreaseQuantity"
                                                                type="button"
                                                                id="decreaseQuantity"
                                                                data-index="${index}"
                                                            >
                                                                -
                                                            </button>
                                                            <input
                                                                type="number"
                                                                value="${quantity}"
                                                                min="1"
                                                                id="quantityInput"
                                                                class="quantityInput form-control form-control-sm text-center bg-transparent"
                                                                data-index="${index}"
                                                            />
                                                            <button
                                                                class="btn btn-outline-secondary increaseQuantity"
                                                                type="button"
                                                                id="increaseQuantity"
                                                                data-index="${index}"
                                                            >
                                                                +
                                                            </button>
                                                        </div>
        </td>
        <td class="text-danger fw-bold">${totalSum}</td>
        <td class="text-end">
            <button class="btn btn-outline-danger btn-sm"
            onclick="deleteProduct(${orderDetailId})"
            >Xóa</button>
        </td>
    </tr>
`;
        });
        tbody.innerHTML = rows; // Thêm dòng mới vào tbody
        totalElement.textContent = formattedTotal;
        goodMoneyElement.textContent = formattedTotal;
        totalAmountFinalElement.textContent = finalTotalAmount; // tổng số tiền
        tienThieuElement.textContent = finalTotalAmount;
        TotalAmountELement.textContent = finalTotalAmount;
    }
    // ẩn nút cập nhật địa chỉ
    const updateAddressButton = document.getElementById("updateAddress");
    if (updateAddressButton) {
        updateAddressButton.style.visibility = "hidden";
    }
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
    window.handlePaymentInStore = function () {
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
                const tbtody = document.querySelector(".tablePayment");
                const customerPay = document.getElementById("customerPay");
                const tienThieu = document.getElementById("tienThieu");
                const confirmPayment = document.getElementById("confirmPayment");
                let row = ` 
                            <tr>
                                            <td>1</td>
                                            <td>${formatedTotalAmount}</td>
                                            <td>${data.paymentMethod}</td>
                                            <td>${formattedDate}</td>
                                            <td>${data.transactionStatus}</td>
                            </tr>
                `;
                tbtody.innerHTML = row;
                if (confirmPayment) {
                    confirmPayment.style.display = "none"; // ẨN Nút thanh toán
                }
                if (customerPay) {
                    customerPay.textContent = formatedTotalAmount;
                }
                if (tienThieu) {
                    tienThieu.textContent = "0 VND";
                }
                createToast("success", "fa-solid fa-circle-check", "Thành công", "Thanh toán thành công");
            })
            .catch((err) => {
                createToast("error", "fa-solid fa-circle-exclamation", "Lỗi", "Thanh toán thất bại!");
                console.error("Lỗi:", err);
            });
    };

    // click vào giao hàng
    const shipping = document.getElementById("shipping");
    const infoCustomer = document.getElementById("infoCustomer");
    const feeShipping = document.getElementById("feeShipping");
    const shippingKey = `isShippingChecked_${orderId}`;
    infoCustomer.style.display = "none";
    const savedShippingState = localStorage.getItem(shippingKey);

    if (savedShippingState === "true") {
        shipping.checked = true;
        infoCustomer.style.display = "block";
        feeShipping.textContent = "32.000 VND";
    } else {
        shipping.checked = false;
        infoCustomer.style.display = "none";
        feeShipping.textContent = "0 VND";
    }
    shipping.addEventListener("change", function () {
        const isChecked = this.checked;
        localStorage.setItem(shippingKey, isChecked); // lưu trạng thái kèm orderId
        if (isChecked) {
            infoCustomer.style.display = "block";
            feeShipping.textContent = "32.000 VND";
        } else {
            infoCustomer.style.display = "none";
            feeShipping.textContent = "0 VND";
        }
        updateTotalAmount(isChecked);
    });
    function updateTotalAmount(isShipping) {
        let shippingValue = isShipping ? 32.0 : 32.0;
        fetch(`/api/admin/sell/feeShipping/${orderId}/${shippingValue}/${isShipping}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then((reponse) => {
                if (!reponse.ok) {
                    throw new Error("Lỗi server không trả dữ liệu về!");
                }
                return reponse.json();
            })
            .then((data) => {
                const { totalAmount } = data;
                const formatedTotalAmount = `${totalAmount.toLocaleString("vi-VN")}.000 VND`;
                totalAmountFinalElement.textContent = formatedTotalAmount;
                tienThieuElement.textContent = formatedTotalAmount;
                TotalAmountELement.textContent = formatedTotalAmount;
            })
            .catch((err) => {
                console.error("Lỗi: " + err);
            });
    }
    // hàm xác nhận đặt hàng
    window.confirmOrderInStore = function () {
        if (shipping.checked) {
            let title = "Xác nhận đơn tại quầy với hình thức giao hàng?";
            updateOrderInStore(title, 1);
        } else {
            let title = "Xác nhận đơn tại quầy với hình thức nhận hàng tại quầy?";
            updateOrderInStore(title, 6);
        }
    };
    // hàm xác nhận đặt hàng
    function updateOrderInStore(title, status) {
        Swal.fire({
            title: `${title}`,
            text: "",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Xác nhận",
            cancelButtonText: "Hủy",
        }).then((result) => {
            if (result.isConfirmed) {
                fetch(`/api/admin/sell/updateOrder/${orderId}/${status}`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                    },
                })
                    .then((reponse) => {
                        if (!reponse.ok) {
                            throw new Error("Lỗi server không trả dữ liệu về!");
                        }
                        return reponse.text();
                    })
                    .then((message) => {
                        console.log(message);
                        window.location.href = "/admin/sell?page=0&size=10";
                    })
                    .catch((err) => {
                        console.error("Lỗi: " + err);
                    });
            }
        });
    }
    // Chọn khách hàng cho đơn vận Chuyển
    window.selectCustomerForOrder = function (button) {
        const rows = button.closest("tr");
        const fullname = rows.cells[2].textContent.trim();
        const customerId = rows.cells[1].textContent.trim();
        const orderData = {
            orderId: orderId,
            customerId: customerId,
        };
        Swal.fire({
            title: `Xác nhận chọn khách hàng ${fullname}?`,
            text: `Đơn hàng sẽ được vận chuyển theo địa chỉ của khách hàng ${fullname}`,
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Xác nhận",
            cancelButtonText: "Hủy",
        }).then((result) => {
            if (result.isConfirmed) {
                fetch("/api/admin/sell/selectCustomerForOrder", {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(orderData),
                })
                    .then((reponse) => {
                        if (!reponse.ok) {
                            throw new Error("Lỗi server không trả dữ liệu về!");
                        }
                        return reponse.json();
                    })
                    .then((data) => {
                        console.log(data);
                        const { fullname, phoneNumber, street, ward, district, city } = data;
                        const fullnameLabel = document.querySelector(".fullname");
                        const fullnameInput = document.getElementById("floatinghovaten");
                        const phoneNumberInput = document.getElementById("floatingsodienthoai");
                        const streetInput = document.getElementById("floatingstreet");
                        const cityInput = document.getElementById("floatingcity");
                        const districtInput = document.getElementById("floatingdistrict");
                        const wardInput = document.getElementById("floatingward");
                        fullnameLabel.textContent = fullname;
                        fullnameInput.value = fullname;
                        phoneNumberInput.value = phoneNumber;
                        streetInput.value = street;
                        cityInput.value = city;
                        districtInput.value = district;
                        wardInput.value = ward;
                        createToast(
                            "success",
                            "fa-solid fa-circle-check",
                            "Thành công",
                            "Chọn khách hàng cho đơn giao hàng thành công"
                        );
                    })
                    .catch((err) => {
                        console.error("Lỗi: " + err);
                        createToast(
                            "error",
                            "fa-solid fa-circle-exclamation",
                            "Lỗi",
                            "Chọn khách hàng cho đơn giao hàng thất bại!"
                        );
                    });
            }
        });
    };
    //  giới tính nam và nữ
    const radioNamAndNu = document.querySelectorAll("input[name='gridRadios']");
    let gender = true; // mặc định là nam
    if (radioNamAndNu) {
        radioNamAndNu.forEach(function (radio) {
            radio.addEventListener("change", function () {
                if (document.getElementById("radionam").checked) {
                    gender = true; // 1 là nam
                } else if (document.getElementById("radionu").checked) {
                    gender = false; // 0 là nữ
                }
            });
        });
    }
    // lắng nghe địa chỉ
    const city = document.querySelector(".floatingcity");
    const district = document.querySelector(".floatingdistrict");
    const ward = document.querySelector(".floatingward");

    let cityValue;
    let districtValue;
    let wardValue;
    city.addEventListener("change", function () {
        cityValue = city.options[city.selectedIndex].textContent.trim();
    });
    district.addEventListener("change", function () {
        districtValue = district.options[district.selectedIndex].textContent.trim();
    });
    ward.addEventListener("change", function () {
        wardValue = ward.options[ward.selectedIndex].textContent.trim();
    });
    // kiểm tra địa chỉ email
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
    // Thêm khách hàng
    window.insertCustomer = function () {
        const fullname = document.getElementById("floatingtenkhachhang").value.trim();
        const phoneNumber = document.getElementById("floatingphoneNumber").value.trim();
        const street = document.getElementById("streetAddress").value.trim();
        const customerData = {
            fullname: fullname,
            phoneNumber: phoneNumber,
            gender: gender,
            street: street,
            ward: wardValue,
            district: districtValue,
            city: cityValue,
        };
        if (fullname && phoneNumber && street && wardValue && districtValue && cityValue) {
            fetch("/admin/sell/addCustomer", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(customerData),
            })
                .then((reponse) => {
                    if (!reponse.ok) {
                        throw new Error("Lỗi server không trả dữ liệu về!");
                    }
                    return reponse.text();
                })
                .then((html) => {
                    // Cập nhật bảng c trong modal với kết quả mới
                    document.querySelector(".dscustomer").innerHTML = html;
                    createToast("success", "fa-solid fa-circle-check", "Thành công", "Thêm khách hàng thành công");
                })
                .catch((err) => {
                    console.error("Lỗi: " + err);
                    createToast("error", "fa-solid fa-circle-exclamation", "Lỗi", "Thêm khách hàng thất bại!");
                });
        } else {
            createToast(
                "error",
                "fa-solid fa-circle-exclamation",
                "Lỗi",
                "Lỗi thêm khách hàng! Vui lòng nhập đầy đủ thông tin!"
            );
            return;
        }
    };
    // tìm kiếm khách hàng
    document.querySelector("#searchCustomerForm").addEventListener("submit", (e) => {
        e.preventDefault();
        const page = document.querySelector('input[name="page"]').value;
        const size = document.querySelector('input[name="size"]').value;
        const searchByCustomer = document.getElementById("searchByCustomer").value;
        fetch(`/admin/sell/searchByCustomer?value=${searchByCustomer}&page=${page}&size=${size}`)
            .then((response) => response.text())
            .then((html) => {
                // Cập nhật bảng c trong modal với kết quả mới
                document.querySelector(".dscustomer").innerHTML = html;
            })
            .catch((err) => {
                console.error("Lỗi khi tìm kiếm:", err);
            });
    });
    // tìm kiếm sản  phẩm
    document.querySelector("#searchForm").addEventListener("submit", (e) => {
        e.preventDefault();

        const value = document.querySelector('input[name="value"]').value;
        fetch(`/admin/sell/search?value=${value}`)
            .then((response) => response.text())
            .then((html) => {
                // Cập nhật bảng sản phẩm trong modal với kết quả mới
                document.querySelector(".dssp").innerHTML = html;
            })
            .catch((err) => {
                console.error("Lỗi khi tìm kiếm:", error);
            });
    });
    // lọc sản phẩm theo kích cỡ
    document.getElementById("sizeSelect").addEventListener("change", () => {
        const sizeId = document.getElementById("sizeSelect").value;
        fetch(`/admin/sell/searchBySize?size=${sizeId}`)
            .then((response) => {
                return response.text();
            })
            .then((html) => {
                // Cập nhật bảng sản phẩm trong modal với kết quả mới
                document.querySelector(".dssp").innerHTML = html;
            })
            .catch((err) => {
                console.error("Lỗi lọc sản phẩm:", error);
            });
    });
    // lọc sản phẩm theo màu
    document.getElementById("colorSelect").addEventListener("change", () => {
        const colorId = document.getElementById("colorSelect").value;

        fetch(`/admin/sell/searchByColor?color=${colorId}`)
            .then((response) => {
                return response.text();
            })
            .then((html) => {
                // Cập nhật bảng sản phẩm trong modal với kết quả mới
                document.querySelector(".dssp").innerHTML = html;
            })
            .catch((err) => {
                console.error("Lỗi lọc sản phẩm:", error);
            });
    });
    // lọc sản phẩm theo danh mục
    document.getElementById("categorySelect").addEventListener("change", () => {
        const categoryId = document.getElementById("categorySelect").value;

        fetch(`/admin/sell/searchByCategory?category=${categoryId}`)
            .then((response) => {
                return response.text();
            })
            .then((html) => {
                // Cập nhật bảng sản phẩm trong modal với kết quả mới
                document.querySelector(".dssp").innerHTML = html;
            })
            .catch((err) => {
                console.error("Lỗi lọc sản phẩm:", error);
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
