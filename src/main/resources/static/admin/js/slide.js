// Hiển thị ảnh bổ sung khi chọn ảnh
function previewAdditionalImages(event) {
    const files = event.target.files;
    const previewContainer = document.getElementById('additionalImagesPreview');

    // Xóa nội dung cũ của preview container
    previewContainer.innerHTML = '';

    for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const reader = new FileReader();

        reader.onload = function(e) {
            // Tạo thẻ div chứa ảnh và nút X
            const imageContainer = document.createElement('div');
            imageContainer.classList.add('position-relative', 'm-2');
            imageContainer.style.width = '150px';
            imageContainer.style.height = '150px';

            // Tạo thẻ img hiển thị ảnh
            const img = document.createElement('img');
            img.src = e.target.result;
            img.classList.add('img-thumbnail');
            img.style.width = '100%';
            img.style.height = '100%';
            img.style.objectFit = 'cover';

            // Tạo nút X để xóa ảnh
            const deleteButton = document.createElement('button');
            deleteButton.innerText = 'X';
            deleteButton.classList.add('btn', 'btn-danger', 'position-absolute');
            deleteButton.style.top = '5px';
            deleteButton.style.right = '5px';
            deleteButton.style.padding = '2px 8px';

            // Thêm sự kiện xóa vào nút X
            deleteButton.addEventListener('click', function() {
                imageContainer.remove();  // Xóa ảnh khi nhấn nút X
            });

            // Thêm img và deleteButton vào imageContainer
            imageContainer.appendChild(img);
            imageContainer.appendChild(deleteButton);

            // Thêm imageContainer vào previewContainer
            previewContainer.appendChild(imageContainer);
        };

        reader.readAsDataURL(file);
    }
}

// Hàm xem trước ảnh đại diện
function previewMainImage(event) {
	var reader = new FileReader();
	reader.onload = function() {
		var output = document.getElementById('mainImagePreview');
		output.src = reader.result;
	};
	reader.readAsDataURL(event.target.files[0]);
}


// Lưu ảnh
document.getElementById("saveImagesButton").addEventListener("click", function() {
	const mainImage = document.getElementById("mainImageInput").files[0];
	const additionalImages = document.getElementById("additionalImages").files;

	if (!mainImage && additionalImages.length === 0) {
		Swal.fire({
			title: "Lỗi!",
			text: "Vui lòng chọn ảnh trước khi lưu.",
			icon: "error",
		});
		return; // Ngừng nếu không có ảnh nào được chọn.
	}

	const formData = new FormData();

	// Lưu ảnh đại diện
	if (mainImage) {
		formData.append("mainImage", mainImage);
	}

	// Lưu ảnh bổ sung
	for (const file of additionalImages) {
		formData.append("additionalImages", file);
	}

	// Gửi yêu cầu lưu ảnh
	fetch("/api/slides/upload", {
		method: "POST",
		body: formData,
	})
		.then(response => response.json())
		.then(data => {
			if (data.success) {
				Swal.fire({
					title: "Thành công!",
					text: "Ảnh đã được lưu thành công!",
					icon: "success",
				}).then(() => {
					window.location.href = "/yourstyle/home"; // Thay đổi đường dẫn trang theo nhu cầu
					window.reload();
				});
			} else {
				Swal.fire({
					title: "Có lỗi xảy ra!",
					text: "Lỗi: " + data.message,
					icon: "error",
				});
			}
		})
		.catch(error => {
			Swal.fire({
				title: "Có lỗi xảy ra!",
				text: "Lỗi: " + error.message,
				icon: "error",
			});
		});
});



