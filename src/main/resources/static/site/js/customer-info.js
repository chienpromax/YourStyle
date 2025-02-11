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
  const video = document.getElementById("video3");
  const playPauseIcon = document.getElementById("playPauseIcon1");
  const muteUnmuteIcon = document.getElementById("muteUnmuteIcon1");

  playPauseIcon.addEventListener("click", function () {
    if (video.paused) {
      video.play();
      playPauseIcon.classList.remove("fa-play");
      playPauseIcon.classList.add("fa-pause");
    } else {
      video.pause();
      playPauseIcon.classList.remove("fa-pause");
      playPauseIcon.classList.add("fa-play");
    }
  });

  muteUnmuteIcon.addEventListener("click", function () {
    video.muted = !video.muted;
    if (video.muted) {
      muteUnmuteIcon.classList.remove("fa-volume-up");
      muteUnmuteIcon.classList.add("fa-volume-mute");
    } else {
      muteUnmuteIcon.classList.remove("fa-volume-mute");
      muteUnmuteIcon.classList.add("fa-volume-up");
    }
  });
});

document.addEventListener("DOMContentLoaded", function () {
  const form = document.querySelector(".info-customer-form");

  form.addEventListener("submit", async function (event) {
    event.preventDefault(); // Ngăn gửi biểu mẫu mặc định

    const phoneInput = document.getElementById("floatingsodienthoai");
    const phoneNumber = phoneInput.value;
    const customerId =
      document.querySelector('input[name="customerId"]')?.value || null;
    const birthdayInput = document.querySelector('input[name="birthday"]');
    const birthday = new Date(birthdayInput.value);
    const fullname = document.querySelector('input[name="fullname"]').value;

    // Kiểm tra định dạng số điện thoại
    const phoneRegex = /^(0[1-9][0-9]{8,9})$/;
    if (!phoneRegex.test(phoneNumber)) {
      Swal.fire({
        icon: "error",
        title: "Số điện thoại không hợp lệ",
        text: "Vui lòng nhập lại.",
      });
      return;
    }

    // Kiểm tra ngày sinh
    const today = new Date();
    const minDate = new Date(
      today.getFullYear() - 1,
      today.getMonth(),
      today.getDate()
    );
    if (birthday > minDate) {
      Swal.fire({
        icon: "error",
        title: "Ngày sinh không hợp lệ",
        text: "Ngày sinh phải lớn hơn 1 năm so với ngày hiện tại.",
      });
      return;
    }

    // Kiểm tra độ dài tên
    if (fullname.length > 50) {
      Swal.fire({
        icon: "error",
        title: "Độ dài họ và tên không hợp lệ",
        text: "Họ và tên không được vượt quá 50 ký tự.",
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
  const fileInput = document.getElementById("avatarInput");
  const avatarImage = document.getElementById("avatarImage");

  const isAvatarPresent =
    avatarImage.src && !avatarImage.src.includes("user-icon.png"); // Kiểm tra nếu avatar khác ảnh mặc định

  if (!fileInput.value && !isAvatarPresent) {
    Swal.fire({
      icon: "warning",
      title: "Chưa chọn ảnh",
      text: "Vui lòng chọn ảnh trước khi xác nhận!",
      confirmButtonText: "OK",
    });
    return false; // Ngăn submit form
  }

  return true;
}
