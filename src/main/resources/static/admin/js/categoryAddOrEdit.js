document.addEventListener("DOMContentLoaded", function () {
  console.log('Script loaded successfully.');

  const elementInputs = document.querySelectorAll(".form-check-input");
  elementInputs.forEach((elementInput) => {
    console.log(elementInput);

    elementInput.addEventListener("change", function () {
      // lấy giá trị categoryId từ thuộc tính data-category-id của checkbox
      const categoryId = this.getAttribute("data-category-id");
      console.log('data' + categoryId)
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
        console.log(`Trạng thái danh mục có categoryId = ${categoryId} đã được cập nhật`);
      } else {
        console.error("Cập nhật thất bại!");
      }
    })
    .catch((error) => {
      console.error("Có lỗi xảy ra:", error);
    });
}

// ảnh
const clickImage = document.getElementById("image");
const inputImage = document.getElementById("imageInput");

// xử lý click mở file ảnh
clickImage.addEventListener("click", function () {
  inputImage.click();
});
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
  output.onload = function() {
      URL.revokeObjectURL(output.src); // free memory
  }
}

// Kiểm tra nếu ảnh chưa được chọn
document.addEventListener("DOMContentLoaded", function() {
  document.getElementById('yourFormId').addEventListener('submit', function(event) {
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
});
