document
  .getElementById("customFileButton")
  .addEventListener("click", function () {
    document.getElementById("additionalImages").click();
  });

document
  .getElementById("saveImagesButton")
  .addEventListener("click", function () {
    const files = document.getElementById("additionalImages").files;
    const formData = new FormData();

    for (const file of files) {
      formData.append("imageFiles", file);
    }

    const productId = document.getElementById("productId").value;
    // const productId = window.location.pathname.split("/").pop(); // Lấy phần cuối của URL

    fetch(`/api/products/${productId}/images`, {
      method: "POST",
      body: formData,
    })
      .then((response) => {
        if (response.ok) {
          Swal.fire({
            title: "Thành công!",
            text: "Ảnh đã được lưu thành công!",
            icon: "success",
            confirmButtonColor: "#3085d6",
            confirmButtonText: "OK",
          }).then((result) => {
            if (result.isConfirmed) {
              location.reload();
            }
          });
        } else {
          return response.text().then((text) => {
            throw new Error(text);
          });
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        Swal.fire({
          title: "Có lỗi xảy ra!",
          text: "Có lỗi xảy ra khi lưu ảnh: ",
          icon: "error",
          confirmButtonColor: "#d33",
          confirmButtonText: "Đóng",
        });
      });
  });

function previewImages(event) {
  const previewContainer = document.getElementById("additionalImagesPreview");
  previewContainer.innerHTML = "";

  const files = event.target.files;

  for (const file of files) {
    const reader = new FileReader();

    reader.onload = function (e) {
      const imgContainer = document.createElement("div");
      imgContainer.classList.add("img-container");

      const img = document.createElement("img");
      img.src = e.target.result;
      img.classList.add("preview-image");

      const deleteButton = document.createElement("button");
      deleteButton.innerText = "X";
      deleteButton.classList.add("delete-button");

      deleteButton.onclick = function () {
        previewContainer.removeChild(imgContainer);
      };

      imgContainer.appendChild(img);
      imgContainer.appendChild(deleteButton);
      previewContainer.appendChild(imgContainer);

      imgContainer.addEventListener("mouseenter", function () {
        deleteButton.style.display = "block";
      });
      imgContainer.addEventListener("mouseleave", function () {
        deleteButton.style.display = "none";
      });
    };

    reader.readAsDataURL(file);
  }
}

function confirmDeleteImage(imageId) {
  console.log("Image ID:", imageId);
  Swal.fire({
    title: "Bạn có chắc chắn muốn xóa hình ảnh này?",
    text: "Bạn sẽ không thể hoàn tác sau khi xóa!",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Xóa!",
    cancelButtonText: "Hủy",
  }).then((result) => {
    if (result.isConfirmed) {
      fetch(`/api/products/delete/${imageId}`, {
        method: "DELETE",
      }).then((response) => {
        console.log(`Fetching: /api/products/delete/${imageId}`);
        if (response.ok) {
          Swal.fire("Đã xóa!", "Hình ảnh đã được xóa thành công.", "success");
          setTimeout(() => {
            location.reload();
          }, 1500);
        } else {
          Swal.fire("Lỗi!", "Có lỗi xảy ra khi xóa hình ảnh.", "error");
        }
      });
    }
  });
}
