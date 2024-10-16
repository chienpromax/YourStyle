document.addEventListener("DOMContentLoaded", function () {
    const elementInputs = document.querySelectorAll(".form-check-input");
    elementInputs.forEach((elementInput) => {
      elementInput.addEventListener("change", function () {
        // tìm hàng tr gần nhất chứa checkbox
        const parentRow = this.closest("tr");
        if (parentRow) {
          const categoryIdElement = parentRow.querySelector(".category-id"); // tìm phần tử có class là category-id bên trong tr
          if (categoryIdElement) {
            const categoryIdValue = categoryIdElement.innerText; // lấy giá trị
            // Lấy phần tử label tương ứng với checkbox thông qua for
            const labelElement = parentRow.querySelector(
              "label[for='" + this.id + "']"
            );
            if (labelElement) {
              if (this.checked) {
                labelElement.textContent = "Hoạt động";
              } else {
                labelElement.textContent = "Bị khóa";
              }
            }
            // gọi hàm
            toggleStatus(categoryIdValue);
          }
        }
      });
    });
  });
  function toggleStatus(categoryId) {
    const statusToggleElement = document.getElementById(
      `statusToggle${categoryId}`
    );
    const newStatus = statusToggleElement.checked;
  
    // Gửi yêu cầu để cập nhật trạng thái
    fetch(`/admin/categorys/toggleStatus/${categoryId}`, {
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
            `Trạng thái danh mục có categoryId = ${categoryId} đã được cập nhật`
          );
        } else {
          console.error("Cập nhật thất bại!");
        }
      })
      .catch((error) => {
        console.error("Có lỗi xảy ra:", error);
      });
  }