const clickImage = document.getElementById("avatarImage");
const inputImage = document.getElementById("avatarInput");

// xử lý click mở file ảnh
clickImage.addEventListener("click", function () {
  inputImage.click();
});
// xử lý load ảnh lên giao diện
function loadFile(event) {
  const image = document.getElementById("avatarImage");
  image.src = URL.createObjectURL(event.target.files[0]);
}
// Hàm xử lý khi click vào icon Set mặc định
document.addEventListener("click", function (event) {
  // xác định phần tử mà mình click vào có class btnDefault
  if (event.target.closest(".btnDefault")) {
    event.preventDefault(); // ngăn reload
    // lấy tất cả các icon bi-star-fill đổi thành bi-star
    const allIcons = document.querySelectorAll(".bi-star-fill");
    allIcons.forEach((icon) => {
      icon.classList.remove("bi-star-fill");
      icon.classList.add("bi-star");
    });
    // xác định phần tử mà mình click vào có class btnDefault
    const clickedIcon = event.target.closest(".btnDefault").querySelector("i");
    clickedIcon.classList.remove("bi-star");
    clickedIcon.classList.add("bi-star-fill");
  }
});

document.addEventListener("DOMContentLoaded", function () {
  const form = document.querySelector(".info-customer-formAdmin");
  form.addEventListener("submit", async function (event) {
    event.preventDefault(); // Ngăn gửi biểu mẫu mặc định

    const phoneInput = document.getElementById("floatingsodienthoai");
    const phoneNumber = phoneInput.value;
    const customerId =
      document.querySelector('input[name="customerId"]')?.value || null;

    // Kiểm tra định dạng số điện thoại
    const phoneRegex = /^(0[1-9][0-9]{8,9})$/;
    if (!phoneRegex.test(phoneNumber)) {
      Swal.fire({
        icon: "error",
        title: "Số điện thoại không hợp lệ",
        text: "Vui lòng nhập số điện thoại đúng định dạng.",
      });
      return;
    }

    // Kiểm tra số điện thoại đã tồn tại qua API
    try {
      const response = await fetch(
        `/api/customers/check-phone?phoneNumber=${phoneNumber}&customerId=${
          customerId || ""
        }`
      );
      const data = await response.json();

      if (data.exists) {
        Swal.fire({
          icon: "error",
          title: "Số điện thoại đã tồn tại",
          text: "Vui lòng sử dụng số điện thoại khác.",
        });
        return;
      }

      // Nếu không có lỗi, gửi biểu mẫu
      form.submit();
    } catch (error) {
      console.error("Lỗi kiểm tra số điện thoại:", error);
      Swal.fire({
        icon: "error",
        title: "Lỗi hệ thống",
        text: "Không thể kiểm tra số điện thoại. Vui lòng thử lại sau.",
      });
    }
  });
});

function validateImage() {
  const fileInput = document.getElementById('avatarInput');
  const avatarImage = document.getElementById('avatarImage');

  const isDefaultImage = avatarImage.src.includes('computer-mouse-click-transparent-free-png.webp');

  // Nếu không chọn file mới và ảnh hiện tại là mặc định
  if (!fileInput.value && isDefaultImage) {
    Swal.fire({
      icon: 'warning',
      title: 'Chưa chọn ảnh',
      text: 'Vui lòng chọn ảnh trước khi xác nhận!',
      confirmButtonText: 'OK',
    });
    return false; // Ngăn submit form
  }
  return true; // Cho phép submit form
}