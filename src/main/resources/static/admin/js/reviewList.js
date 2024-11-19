function toggleSelectAll(source) {
    var checkboxes = document.querySelectorAll('.review-checkbox');
    checkboxes.forEach(function(checkbox) {
        checkbox.checked = source.checked;
    });
}
		//lấy reviewId
function getSelectedReviewIds() {
    const selectedCheckboxes = document.querySelectorAll('.review-checkbox:checked');
    const selectedReviewIds = Array.from(selectedCheckboxes).map(checkbox => {
        // Trích xuất reviewId từ thuộc tính name, giả sử nó có dạng 'reviewIds[<reviewId>]'
        const match = checkbox.name.match(/reviewIds\[(\d+)\]/);
        return match ? match[1] : null;
    }).filter(id => id !== null);
    return selectedReviewIds;
}

            // Xóa nhiều đánh giá
            function confirmDeleteMultiple() {
            	const selectedReviews = Array.from(document.querySelectorAll('.review-checkbox:checked')).map(checkbox => {
            	    const reviewId = checkbox.closest('tr').querySelector('.review-id').textContent.trim();
            	    return reviewId;
            	});
                if (selectedReviews.length === 0) {
                    ('Vui lòng chọn ít nhất một đánh giá để xóa.');
                    return;
                }

                Swal.fire({
                    title: 'Bạn có chắc chắn?',
                    text: 'Bạn sẽ không thể phục hồi đánh giá đã xóa!',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: 'Có, xóa!',
                    cancelButtonText: 'Hủy'
                }).then((result) => {
                    if (result.isConfirmed) {
                        // Gửi yêu cầu xóa tới server
                        fetch('/admin/reviews/deleteMultiple', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded',
                            },
                            body: new URLSearchParams({
                                'reviewIds': selectedReviews.join(',')
                            })
                        })
                        .then(response => response.json())  // Đảm bảo phản hồi được phân tích dưới dạng JSON
                        .then(data => {
                            if (data.message) {
                                Swal.fire('Thành công!', data.message, 'success');
                                location.reload(); 
                            } else {
                                Swal.fire('Lỗi!', data.message || 'Có lỗi xảy ra khi xóa các đánh giá.', 'error');
                            }
                        })
                        .catch(error => {
                            Swal.fire('Lỗi!', 'Không thể kết nối tới server.', 'error');
                        });
                    }
                });
            }

            // Xóa một đánh giá
            function deleteReview(reviewId) {
                Swal.fire({
                    title: 'Bạn có chắc chắn?',
                    text: 'Bạn sẽ không thể phục hồi đánh giá đã xóa!',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: 'Có, xóa!',
                    cancelButtonText: 'Hủy'
                }).then((result) => {
                    if (result.isConfirmed) {
                        fetch(`/admin/reviews/delete/${reviewId}`, {
                            method: 'GET'
                        })
                        .then(response => response.text())
                        .then(result => {
                            Swal.fire('Thành công!', 'Đã xóa đánh giá.', 'success');
                            location.reload(); // Tải lại trang sau khi xóa
                        })
                        .catch(error => {
                            Swal.fire('Lỗi!', 'Có lỗi xảy ra khi xóa đánh giá.', 'error');
                        });
                    }
                });
            }