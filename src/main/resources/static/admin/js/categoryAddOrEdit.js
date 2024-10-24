document.addEventListener("DOMContentLoaded", function () {

  const elementInputs = document.querySelectorAll(".form-check-input");
  elementInputs.forEach((elementInput) => {


    elementInput.addEventListener("change", function () {
      // lấy giá trị categoryId từ thuộc tính data-category-id của checkbox
      const categoryId = this.getAttribute("data-category-id");

      if (categoryId) {
        // Lấy phần tử label tương ứng với checkbox thông qua for
        const labelElement = document.querySelector("label[for='" + this.id + "']");
        if (labelElement) {
          labelElement.textContent = this.checked ? "Hoạt động" : "Bị khóa";
        }
        // Gọi hàm để cập nhật trạng thái
        toggleStatus(categoryId);
      }
    });
  });
});

function toggleStatus(categoryId) {
  const statusToggleElement = document.getElementById(`statusToggle${categoryId}`);
  const newStatus = statusToggleElement.checked;

  // Gửi yêu cầu để cập nhật trạng thái
  fetch(`/admin/categories/toggleStatus/${categoryId}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ status: newStatus }),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {

      } else {
        console.error("Cập nhật thất bại!");
      }
    })
    .catch((error) => {
      console.error("Có lỗi xảy ra:", error);
    });
}

// xử lý load ảnh lên giao diện

function loadFile(event) {
  const image = document.getElementById("image");
  image.src = URL.createObjectURL(event.target.files[0]);

  var errorMessage = document.getElementById('error-message');

  // Hiển thị ảnh được chọn
  image.src = URL.createObjectURL(event.target.files[0]);

  // Ẩn thông báo lỗi nếu đã chọn ảnh
  errorMessage.style.display = 'none';
  var output = document.getElementById('image');
  output.src = URL.createObjectURL(event.target.files[0]);
  output.onload = function () {
    URL.revokeObjectURL(output.src); // free memory
  }
}

// Kiểm tra nếu ảnh chưa được chọn
document.addEventListener("DOMContentLoaded", function () {
  document.getElementById('yourFormId').addEventListener('submit', function (event) {
    var imageInput = document.getElementById('imageInput');
    var currentImage = document.getElementById('image').src;
    var errorMessage = document.getElementById('error-message');

    // Kiểm tra nếu ảnh chưa được chọn
    if (currentImage.includes('default-image-path.jpg') && imageInput.files.length === 0) {
      errorMessage.textContent = "Vui lòng chọn ảnh cho danh mục.";
      errorMessage.style.display = "block"; // Hiển thị thông báo lỗi
      event.preventDefault(); // Ngăn không cho form submit nếu chưa chọn ảnh
    } else {
      errorMessage.style.display = "none"; // Ẩn thông báo lỗi nếu không có lỗi
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
      const url = new URL(window.location.href); // lấy đường dẫn
      url.searchParams.delete("messageType"); // tìm messageType và xóa nó đi
      url.searchParams.delete("messageContent"); // tìm messageContent và xóa nó đi
      window.history.replaceState(null, "", url.href); // Cập nhật url trên trình duyệt mà không tải lại trang
    }, 5000);
  }
});


