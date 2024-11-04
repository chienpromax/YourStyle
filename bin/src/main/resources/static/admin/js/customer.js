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
