// function previewAvatar(event) {
//     const file = event.target.files[0];
//     if (file) {
//         const reader = new FileReader();
//         reader.onload = function(e) {
//             const avatarImg = document.getElementById("avatar-img");
//             avatarImg.src = e.target.result;
//         };
//         reader.readAsDataURL(file);
//     }
// }

// function updateEmail() {
//     const email = document.getElementById("email").value;
//     const accountId = document.getElementById("accountId").value;

//     if (!validateEmail(email)) {
//         Swal.fire({
//             title: "Lỗi!",
//             text: "Vui lòng nhập địa chỉ email hợp lệ.",
//             icon: "error",
//         });
//         return;
//     }

//     fetch("/yourstyle/accounts/update-email", {
//         method: "POST",
//         headers: {
//             "Content-Type": "application/x-www-form-urlencoded",
//         },
//         body: `accountId=${encodeURIComponent(accountId)}&newEmail=${encodeURIComponent(email)}`
//     })
//     .then(response => response.json())
//     .then(data => {
//         if (data.message) {
//             Swal.fire({
//                 title: data.message,
//                 icon: "success",
//             });
//         }
//     })
//     .catch(error => {
//         console.error("Lỗi:", error);
//         Swal.fire({
//             title: "Có lỗi xảy ra!",
//             text: "Đã xảy ra lỗi trong quá trình cập nhật.",
//             icon: "error",
//         });
//     });
// }

// function validateEmail(email) {
//     const regex = /^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$/;
//     return regex.test(email);
// }

// function updatePhoneNumber() {
//     const phone = document.getElementById("phone").value;
//     const accountId = document.getElementById("accountId").value;

//     if (!validatePhoneNumber(phone)) {
//         Swal.fire({
//             title: "Lỗi!",
//             text: "Vui lòng nhập số điện thoại hợp lệ.",
//             icon: "error",
//         });
//         return;
//     }

//     fetch("/yourstyle/accounts/update-phone", {
//         method: "POST",
//         headers: {
//             "Content-Type": "application/x-www-form-urlencoded",
//         },
//         body: `accountId=${encodeURIComponent(accountId)}&newPhone=${encodeURIComponent(phone)}`
//     })
//     .then(response => response.json())
//     .then(data => {
//         if (data.message) {
//             Swal.fire({
//                 title: data.message,
//                 icon: "success",
//             });
//         }
//     })
//     .catch(error => {
//         console.error("Lỗi:", error);
//         Swal.fire({
//             title: "Có lỗi xảy ra!",
//             text: "Đã xảy ra lỗi trong quá trình cập nhật.",
//             icon: "error",
//         });
//     });
// }

// function validatePhoneNumber(phone) {
//     const regex = /^[0-9]{10,15}$/;
//     return regex.test(phone);
// }

// function updateProfile() {
//     const fullName = document.getElementById("fullname").value;
//     const birthday = document.getElementById("birthday").value;
//     const gender = document.getElementById("gender").value;
//     const nationality = document.getElementById("nationality").value;
//     const accountId = document.getElementById("accountId").value;
//     const avatarFile = document.getElementById("avatar-upload").files[0];

//     if (!fullName || !birthday || !gender || !nationality) {
//         Swal.fire({
//             title: "Lỗi!",
//             text: "Vui lòng điền đầy đủ thông tin.",
//             icon: "error",
//         });
//         return;
//     }

//     const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
//     if (!dateRegex.test(birthday)) {
//         Swal.fire({
//             title: "Lỗi!",
//             text: "Vui lòng nhập ngày sinh hợp lệ.",
//             icon: "error",
//         });
//         return;
//     }

//     const formData = new FormData();
//     formData.append("accountId", accountId);
//     formData.append("fullName", fullName);
//     formData.append("birthday", birthday);
//     formData.append("gender", gender);
//     formData.append("nationality", nationality);

//     if (avatarFile) {
//         formData.append("avatar", avatarFile);
//     } else {
//         console.log("Không có ảnh đại diện được chọn.");
//     }

//     fetch("/yourstyle/accounts/update-profile", {
//         method: "POST",
//         body: formData
//     })
//     .then(response => response.json())
//     .then(data => {
//         if (data.message) {
//             Swal.fire({
//                 title: data.message,
//                 icon: "success",
//             });
//         }
//     })
//     .catch(error => {
//         console.error("Lỗi:", error);
//         Swal.fire({
//             title: "Có lỗi xảy ra!",
//             text: "Đã xảy ra lỗi trong quá trình cập nhật.",
//             icon: "error",
//         });
//     });
// }
