window.addEventListener("DOMContentLoaded", function () {
    const minPrice = document.getElementById("minPrice");
    const maxPrice = document.getElementById("maxPrice");
    const urlParams = new URLSearchParams(window.location.search);
    const categoryId = urlParams.get("categoryId");
    window.filterMinPriceAndMaxPrice = function () {
        if (minPrice.value.trim() == "") {
            alert("Vui lòng nhập giá thấp nhất!!!");
            minPrice.focus();
            return;
        }
        if (maxPrice.value.trim() == "") {
            alert("Vui lòng nhập giá cao nhất!!!");
            maxPrice.focus();
            return;
        }
        if (parseFloat(minPrice.value) > parseFloat(maxPrice.value)) {
            alert("Giá thấp nhất không được lớn hơn giá cao nhất!!!");
            minPrice.value = "";
            minPrice.focus();
            return;
        }
        // loại bỏ dấu . và hàng nghìn
        const minPriceValue = parseFloat(minPrice.value.replace(/\D/g, "")) / 1000;
        const maxPriceValue = parseFloat(maxPrice.value.replace(/\D/g, "")) / 1000;
        fetch(
            `/yourstyle/allproduct/filterMinPriceAndMaxPrice?minPrice=${minPriceValue}&maxPrice=${maxPriceValue}&categoryId=${categoryId}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            }
        )
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Lỗi: ", error);
                }
                return response.text(); // Bạn có thể dùng response.text() nếu chỉ cần thông báo
            })
            .then((html) => {
                const div = document.querySelector(".dsProductByCategory");
                div.innerHTML = html;
            })
            .catch((err) => {
                console.error("Lỗi:", err);
            });
    };
    minPrice.addEventListener("input", function () {
        const inputField = this;
        let value = inputField.value.replace(/\D/g, ""); // Loại bỏ ký tự không phải số

        if (value) {
            value = parseInt(value).toLocaleString("vi-VN");
        }
        inputField.value = value;
    });
    maxPrice.addEventListener("input", function () {
        const inputField = this;
        let value = inputField.value.replace(/\D/g, ""); // Loại bỏ ký tự không phải số

        if (value) {
            value = parseInt(value).toLocaleString("vi-VN");
        }
        inputField.value = value;
    });
    const checkColor = document.querySelector(".colorRadio ");

    const customRange1 = document.getElementById("customRange1");
    const rangeValueElement = document.getElementById("rangeValue");
    if (customRange1) {
        customRange1.addEventListener("input", function (e) {
            const value = Number(e.target.value).toLocaleString("vi-VN");
            rangeValueElement.textContent = value;

            const formattedValue = value.replace(/\D/g, "");
            const filterPrice = Math.floor(formattedValue / 1000);
            fetch(`/yourstyle/allproduct/filterRangePrice?price=${filterPrice}&categoryId=${categoryId}`, {
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
                    const div = document.querySelector(".dsProductByCategory");
                    div.innerHTML = html;
                })
                .catch((err) => {
                    console.error("Lỗi:", err);
                });
        });
    }
    function getFilterUrl(sizeId, colorId, categoryId) {
        let url = `/yourstyle/allproduct/filterProduct?categoryId=${categoryId}`;
        if (sizeId) {
            url += `&sizeId=${sizeId}`;
        }
        if (colorId) {
            url += `&colorId=${colorId}`;
        }
        console.log("request: ", url);
        return url;
    }
    // lọc sản phẩm theo kích thước
    document.querySelectorAll("input[name='size']").forEach(function (inputElement) {
        inputElement.addEventListener("change", function () {
            if (inputElement.checked) {
                const sizeId = inputElement.value;
                const colorId = document.querySelector("input[name='color']:checked")?.value;
                fetch(getFilterUrl(sizeId, colorId, categoryId), {
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
                        const div = document.querySelector(".dsProductByCategory");
                        div.innerHTML = html;
                    })
                    .catch((err) => {
                        console.error("Lỗi:", err);
                    });
            }
        });
    });
    // lọc sản phẩm theo màu
    document.querySelectorAll("input[name='color']").forEach(function (inputElement) {
        inputElement.addEventListener("change", function () {
            if (inputElement.checked) {
                const colorId = inputElement.value;
                const sizeId = document.querySelector("input[name='size']:checked")?.value;
                fetch(getFilterUrl(sizeId, colorId, categoryId), {
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
                        const div = document.querySelector(".dsProductByCategory");
                        div.innerHTML = html;
                    })
                    .catch((err) => {
                        console.error("Lỗi:", err);
                    });
            }
        });
    });
    // sắp xếp sản phẩm theo giá
    document.getElementById("selectSort").addEventListener("change", function () {
        const selectedValue = this.value;
        fetch(`/yourstyle/allproduct/sortProduct?typeSort=${selectedValue}&categoryId=${categoryId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Lỗi: ", error);
                }
                return response.text();
            })
            .then((html) => {
                const div = document.querySelector(".dsProductByCategory");
                div.innerHTML = html;
            })
            .catch((error) => {
                console.log("Lỗi: ", error);
            });
    });
});
