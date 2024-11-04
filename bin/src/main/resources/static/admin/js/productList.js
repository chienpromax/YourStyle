document.addEventListener("DOMContentLoaded", function () {
    const elementInputs = document.querySelectorAll(".form-check-input");
    elementInputs.forEach((elementInput) => {
      elementInput.addEventListener("change", function () {
        // Tìm hàng tr gần nhất chứa checkbox
        const parentRow = this.closest("tr");
        if (parentRow) {
          const productIdElement = parentRow.querySelector(".product-id"); // tìm phần tử chứa ID sản phẩm
          if (productIdElement) {
            const productIdValue = productIdElement.innerText; // lấy giá trị ID sản phẩm
            // Lấy phần tử label tương ứng với checkbox thông qua for
            const labelElement = parentRow.querySelector(
              "label[for='" + this.id + "']"
            );
            if (labelElement) {
              // Thay đổi nhãn dựa trên trạng thái checkbox
              if (this.checked) {
                labelElement.textContent = "Còn hàng";
              } else {
                labelElement.textContent = "Hết hàng";
              }
            }
            // Gọi hàm để cập nhật trạng thái sản phẩm
            toggleProductStatus(productIdValue);
          }
        }
      });
    });
  });
  
  function toggleProductStatus(productId) {
    const statusToggleElement = document.getElementById(
      `statusToggle${productId}`
    );
    const newStatus = statusToggleElement.checked;
  
    // Gửi yêu cầu để cập nhật trạng thái sản phẩm
    fetch(`/admin/products/toggleStatus/${productId}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ status: newStatus }),
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.success) {
          console.log(
            `Trạng thái sản phẩm có productId = ${productId} đã được cập nhật`
          );
        } else {
          console.error("Cập nhật thất bại!");
        }
      })
      .catch((error) => {
        console.error("Có lỗi xảy ra:", error);
      });
  }
  