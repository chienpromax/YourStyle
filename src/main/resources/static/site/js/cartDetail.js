document.addEventListener("DOMContentLoaded", function () {
  const colorOptions = document.querySelectorAll(".color-option");
  const sizeOptions = document.querySelectorAll(".size-option");
  const variantIdInput = document.getElementById("selectedVariantId");
  const colorIdInput = document.getElementById("selectedColorId");
  const sizeIdInput = document.getElementById("selectedSizeId");

  // Hàm cập nhật Variant ID
  function updateVariantId() {
    const selectedColorOption = document.querySelector(
      'input[name="colorOptions"]:checked'
    );
    const selectedSizeOption = document.querySelector(
      'input[name="sizeOptions"]:checked'
    );

    const selectedColorId =
      selectedColorOption?.getAttribute("data-color") || "";
    const selectedSizeId = selectedSizeOption?.getAttribute("data-size") || "";

    // Cập nhật input ẩn
    colorIdInput.value = selectedColorId;
    sizeIdInput.value = selectedSizeId;

    if (selectedColorId && selectedSizeId) {
      const matchingVariant = Array.from(
        document.querySelectorAll(".variant-option")
      ).find(
        (option) =>
          option.getAttribute("data-color-id") === selectedColorId &&
          option.getAttribute("data-size-id") === selectedSizeId
      );

      if (matchingVariant) {
        variantIdInput.value = matchingVariant.getAttribute("data-variant-id");
        console.log("Selected Variant ID:", variantIdInput.value);
      } else {
        variantIdInput.value = "";
        console.warn("Không tìm thấy variant phù hợp!");
      }
    } else {
      variantIdInput.value = "";
    }
  }

  // Lắng nghe sự kiện thay đổi trên màu và kích thước
  colorOptions.forEach((option) =>
    option.addEventListener("change", updateVariantId)
  );
  sizeOptions.forEach((option) =>
    option.addEventListener("change", updateVariantId)
  );
});

function addToCart(event) {
  event.preventDefault();

  const selectedColorId = document.querySelector('input[name="colorOptions"]:checked').dataset.color; // Lấy màu đã chọn
  const selectedSizeId = document.querySelector('input[name="sizeOptions"]:checked').dataset.size; // Lấy kích thước đã chọn

  if (!selectedColorId || !selectedSizeId) {
    Swal.fire({
      icon: "warning",
      title: "Bạn chưa chọn màu hoặc kích thước!",
      text: "Vui lòng chọn đầy đủ màu và kích thước trước khi thêm vào giỏ hàng.",
    });
    return;
  }

  // Cập nhật các giá trị trong các trường ẩn
  document.getElementById("selectedColorId").value = selectedColorId;
  document.getElementById("selectedSizeId").value = selectedSizeId;

  // Lấy productVariantId từ các tùy chọn biến thể
  const variantOptions = document.querySelectorAll('.variant-option');
  let productVariantId = null;
  variantOptions.forEach(option => {
    if (option.dataset.colorId == selectedColorId && option.dataset.sizeId == selectedSizeId) {
      productVariantId = option.dataset.variantId; // Lấy variantId
    }
  });

  if (productVariantId) {
    document.getElementById("selectedVariantId").value = productVariantId; // Cập nhật variantId
  } else {
    Swal.fire({
      icon: "warning",
      title: "Không tìm thấy biến thể sản phẩm tương ứng!",
      text: "Vui lòng chọn lại màu và kích thước.",
    });
    return;
  }

  const formData = new FormData(document.getElementById("addToCartForm"));

  // Gửi yêu cầu AJAX
  fetch("/yourstyle/carts/addtocart", {
    method: "POST",
    body: formData,
  })
    .then((response) => {
      const contentType = response.headers.get("Content-Type");
      if (contentType && contentType.includes("text/html")) {
        window.location.href = "/yourstyle/accounts/login";
        return;
      }
      return response.json();
    })
    .then((data) => {
      if (data && data.success) {
        location.reload();
      } else if (data) {
        Swal.fire({
          icon: "warning",
          title: "Màu hoặc size bạn chọn đã hết hàng",
          text: "Vui lòng chọn màu hoặc size khác của sản phẩm.",
        });
      }
    })
    .catch((error) => {
      console.error("Lỗi:", error.message);
    });
}


// function addToCart(event) {
//   event.preventDefault();

//   const selectedColorId = document.getElementById("selectedColorId").value;
//   const selectedSizeId = document.getElementById("selectedSizeId").value;

//   if (!selectedColorId || !selectedSizeId) {
//     Swal.fire({
//       icon: "warning",
//       title: "Bạn chưa chọn màu hoặc kích thước!",
//       text: "Vui lòng chọn đầy đủ màu và kích thước trước khi thêm vào giỏ hàng.",
//     });
//     return;
//   }

//   const formData = new FormData(document.getElementById("addToCartForm"));

//   // Gửi yêu cầu AJAX
//   fetch("/yourstyle/carts/addtocart", {
//     method: "POST",
//     body: formData,
//   })
//     .then((response) => {
//       const contentType = response.headers.get("Content-Type");
//       if (contentType && contentType.includes("text/html")) {
//         window.location.href = "/yourstyle/accounts/login";
//         return;
//       }
//       return response.json();
//     })
//     .then((data) => {
//       if (data && data.success) {
//         location.reload();
//       } else if (data) {
//         Swal.fire({
//           icon: "warning",
//           title: "Màu hoặc size bạn chọn đã hết hàng",
//           text: "Vui lòng chọn màu hoặc size khác của sản phẩm.",
//         });
//       }
//     })
//     .catch((error) => {
//       console.error("Lỗi:", error.message);
//     });
// }
