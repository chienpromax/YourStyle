window.addEventListener("DOMContentLoaded", () => {
  // xử lý không hiển thị đúng các trường khi mới vào cho tài khoản
  const form = document.querySelector("form");
  const inputs = form.querySelectorAll("input[required]"); // lấy tất cả các ô input có thuộc tính required
  inputs.forEach((input) => {
    if (!input.value) {
      input.classList.remove("is-valid");
      event.preventDefault();
    }
  });
  // xử lý thông báo
  const urlParams = new URLSearchParams(window.location.search); // lấy url trên đường dẫn web
  const messageTypeValue = urlParams.get("messageType");
  const messageContentValue = urlParams.get("messageContent");
  if (messageTypeValue && messageContentValue) {
    if (messageTypeValue === "success") {
      createToast(
        messageTypeValue,
        "fa-solid fa-circle-check",
        "Thành công",
        messageContentValue
      );
    } else if (messageTypeValue === "warning") {
      createToast(
        messageTypeValue,
        "fa-solid fa-triangle-exclamation",
        "Cảnh báo",
        messageContentValue
      );
    } else if (messageTypeValue === "error") {
      createToast(
        messageTypeValue,
        "fa-solid fa-circle-exclamation",
        "Lỗi",
        messageContentValue
      );
    } else if (messageTypeValue === "info") {
      createToast(
        messageTypeValue,
        "fa-solid fa-circle-info",
        "Thông tin",
        messageContentValue
      );
    }
  }
});
function createToast(type, icon, title, text) {
  console.log("toast opened");
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
    const url = new URL(window.location.href); // lấy đường dẫn
    url.searchParams.delete("messageType"); // tìm messageType và xóa nó đi
    url.searchParams.delete("messageContent"); // tìm messageContent và xóa nó đi
    window.history.replaceState(null, "", url.href); // Cập nhật url trên trình duyệt mà không tải lại trang
  }, 5000);
}
