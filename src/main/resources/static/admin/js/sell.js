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
