document.addEventListener("DOMContentLoaded", function () {
  const elementInputs = document.querySelectorAll(".form-check-input");
  elementInputs.forEach((elementInput) => {
    elementInput.addEventListener("change", function () {
      // tìm hàng tr gần nhất chứa checkbox
      const parentRow = this.closest("tr");
      if (parentRow) {
        const accountIdElement = parentRow.querySelector(".account-id"); // tìm phần tử có class là account-id bên trong tr
        if (accountIdElement) {
          const accountIdValue = accountIdElement.innerText; // lấy giá trị
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
          toggleStatus(accountIdValue);
        }
      }
    });
  });
});
function toggleStatus(accountId) {
  const statusToggleElement = document.getElementById(
    `statusToggle${accountId}`
  );
  const newStatus = statusToggleElement.checked;

  // Gửi yêu cầu để cập nhật trạng thái
  fetch(`/admin/accounts/toggleStatus/${accountId}`, {
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
          `Trạng thái tài khoản có accountId = ${accountId} đã được cập nhật`
        );
      } else {
        console.error("Cập nhật thất bại!");
      }
    })
    .catch((error) => {
      console.error("Có lỗi xảy ra:", error);
    });
}
