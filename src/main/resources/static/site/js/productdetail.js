document.addEventListener("DOMContentLoaded", function () {
    const shareProductForm = document.getElementById("shareProductForm");
    const modal = document.getElementById("staticBackdrop");
    const successOverlay = document.getElementById("successOverlay");

    if (shareProductForm) {
        shareProductForm.addEventListener("submit", function (event) {
            event.preventDefault(); // Ngăn submit form mặc định

            const formData = new FormData(shareProductForm);

            // Xóa các thông báo lỗi cũ
            const modalBody = modal.querySelector(".modal-body");
            modalBody.querySelectorAll(".text-danger").forEach(errorText => errorText.remove());

            // Gửi yêu cầu chia sẻ
            fetch(shareProductForm.action, {
                method: shareProductForm.method,
                body: formData
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Hiển thị overlay thành công
                        successOverlay.classList.remove("d-none");

                        // Đóng modal sau 3 giây
                        setTimeout(() => {
                            const modalInstance = bootstrap.Modal.getInstance(modal);
                            modalInstance.hide();
                            successOverlay.classList.add("d-none");
                        }, 3000);
                    } else {
                        if (data.redirectUrl) {
                            // Chuyển hướng đến trang đăng nhập nếu cần
                            window.location.href = data.redirectUrl;
                        } else {
                            // Hiển thị lỗi theo từng trường
                            if (data.fieldErrors) {
                                for (const [field, message] of Object.entries(data.fieldErrors)) {
                                    const fieldInput = document.getElementById(
                                        field === "recipientEmail" ? "recipientEmail" : "senderEmailDisplay"
                                    );
                                    const errorText = document.createElement("div");
                                    errorText.className = "text-danger";
                                    errorText.textContent = message;
                                    fieldInput.parentElement.appendChild(errorText);
                                }
                            } else if (data.errorMessage) {
                                // Hiển thị lỗi chung nếu không có lỗi theo trường
                                const errorDiv = document.createElement("div");
                                errorDiv.className = "text-danger";
                                errorDiv.textContent = data.errorMessage;
                                modalBody.appendChild(errorDiv);
                            }
                        }
                    }
                })
                .catch(error => {
                    // Xử lý lỗi hệ thống
                    const errorDiv = document.createElement("div");
                    errorDiv.className = "text-danger";
                    errorDiv.textContent = `Đã xảy ra lỗi: ${error.message}`;
                    modalBody.appendChild(errorDiv);
                });
        });
    }
});
