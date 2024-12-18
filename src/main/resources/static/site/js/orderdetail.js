function copyVoucherCode(buttonElement) {
  const voucherCode = buttonElement.getAttribute("data-code");

  navigator.clipboard.writeText(voucherCode).then(() => {
    Swal.fire({
      icon: "success",
      title: "Thành công!",
      text: "Đã sao chép mã: " + voucherCode,
      confirmButtonText: "OK",
    });
  }).catch((err) => {
    console.error("Lỗi sao chép:", err);
  });
}


function placeOrder() {
  const paymentMethod = document.querySelector(
    'input[name="paymentMethod"]:checked'
  ).value;

  fetch("/yourstyle/order/place-order", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ paymentMethod: paymentMethod }),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        Swal.fire({
          icon: "success",
          title: "Đặt hàng thành công!",
          text: "Bạn sẽ được chuyển đến lịch sử đơn hàng.",
          confirmButtonText: "OK",
        }).then(() => {
          window.location.href = "/yourstyle/order/orderhistory";
        });
      } else {
        Swal.fire({
          icon: "error",
          title: "Có lỗi xảy ra!",
          text: data.message,
          confirmButtonText: "OK",
        });
      }
    })
    .catch((error) => {
      console.error("Lỗi khi đặt hàng:", error);
      Swal.fire({
        icon: "error",
        title: "Có lỗi xảy ra!",
        text: "Vui lòng thử lại sau.",
        confirmButtonText: "OK",
      });
    });
}

function applyDiscount() {
  const discountCode = document.getElementById("discountCode").value;

  fetch("/yourstyle/order/apply-voucher", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ vouchercode: discountCode }),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        Swal.fire({
          icon: "success",
          title: "Thành công!",
          text: "Mã giảm giá đã được áp dụng.",
          confirmButtonText: "OK",
        });
        location.reload();
      } else {
        // Hiển thị thông báo lỗi từ server
        Swal.fire({
          icon: "error",
          title: "Lỗi!",
          text: data.message || "Có lỗi xảy ra khi áp dụng mã giảm giá.",
          confirmButtonText: "OK",
        });
      }
    })
    .catch((error) => {
      console.error("Lỗi khi áp dụng mã giảm giá:", error);
      Swal.fire({
        icon: "error",
        title: "Lỗi hệ thống!",
        text: "Vui lòng thử lại sau.",
        confirmButtonText: "OK",
      });
    });
}

function confirmDelete(addressId, customerId) {
  Swal.fire({
    title: "Bạn có chắc chắn muốn xóa?",
    text: "Bạn sẽ không thể hoàn tác sau khi xóa!",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Xóa nó!",
    cancelButtonText: "Hủy",
  }).then((result) => {
    if (result.isConfirmed) {
      window.location.href =
        "/yourstyle/carts/orderDetail/deleteAddress/" +
        addressId +
        "?customerId=" +
        customerId;
    }
  });
}

document.addEventListener("DOMContentLoaded", function () {
  const form = document.querySelector(".info-customer-form");
  form.addEventListener("submit", function (event) {
    event.preventDefault(); // Ngăn gửi biểu mẫu mặc định

    const phoneNumber = document
      .querySelector('input[name="phoneNumber"]')
      .value.trim();
    const customerId =
      document.querySelector('input[name="customerId"]')?.value || null;
    const fullname = document
      .querySelector('input[name="fullname"]')
      .value.trim();

    // Kiểm tra định dạng SĐT
    const phoneRegex = /^(0[1-9][0-9]{8,9})$/;
    if (!phoneRegex.test(phoneNumber)) {
      Swal.fire({
        icon: "error",
        title: "Số điện thoại không hợp lệ",
        text: "Vui lòng nhập lại.",
      });
      return;
    }

    // Kiểm tra độ dài tên
    if (fullname.length > 49) {
      Swal.fire({
        icon: "error",
        title: "Độ dài họ và tên không hợp lệ",
        text: "Họ và tên không được vượt quá 49 ký tự.",
      });
      return;
    }

    // Kiểm tra số điện thoại đã tồn tại
    fetch(
      `/api/customers/check-phone?phoneNumber=${phoneNumber}&customerId=${
        customerId || ""
      }`
    )
      .then((response) => response.json())
      .then((data) => {
        console.log("Response data:", data); // Kiểm tra phản hồi từ API
        if (data.exists) {
          Swal.fire({
            icon: "error",
            title: "Số điện thoại đã tồn tại",
            text: "Vui lòng sử dụng số điện thoại khác.",
          }).then(() => {
            location.reload();
          });
        } else {
          form.submit(); // Gửi biểu mẫu nếu không có lỗi
        }
      })
      .catch((error) => {
        console.error("Lỗi kiểm tra số điện thoại:", error);
        Swal.fire({
          icon: "error",
          title: "Lỗi hệ thống",
          text: "Không thể kiểm tra số điện thoại. Vui lòng thử lại sau.",
        });
      });
  });
});

document.getElementById("paymentForm").addEventListener("submit", function(event) {
  event.preventDefault();

  Swal.fire({
      icon: 'info',
      title: 'Đang phát triển',
      text: 'Tính năng này hiện đang được phát triển. Vui lòng quay lại sau.',
      confirmButtonText: 'OK'
  });
});
