document.addEventListener('DOMContentLoaded', function() {
    // Xử lý sự kiện xóa đánh giá đơn
    const deleteLinks = document.querySelectorAll('.delete-review');

    deleteLinks.forEach(link => {
        link.addEventListener('click', function() {
            const reviewId = this.getAttribute('data-id');

            Swal.fire({
                title: 'Bạn có chắc chắn muốn xóa đánh giá này?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Xóa',
                cancelButtonText: 'Hủy'
            }).then(async (result) => {
                if (result.isConfirmed) {
                    try {
                        const response = await fetch(`/admin/reviews/${reviewId}`, {
                            method: 'DELETE',
                        });

                        if (response.ok) {
                            Swal.fire('Đã xóa!', 'Đánh giá đã được xóa thành công.', 'success').then(() => {
                                location.reload();
                            });
                        } else {
                            Swal.fire('Lỗi', 'Không thể xóa đánh giá này.', 'error');
                        }
                    } catch (error) {
                        Swal.fire('Lỗi', 'Không thể xóa đánh giá này.', 'error');
                    }
                }
            });
        });
    });

    // Xóa nhiều đánh giá
    function confirmDeleteMultiple() {
        const selectedReviews = Array.from(document.querySelectorAll('.review-checkbox:checked'))
            .map(checkbox => checkbox.value);

        if (selectedReviews.length > 0) {
            Swal.fire({
                title: 'Bạn có chắc chắn muốn xóa các đánh giá đã chọn?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Xóa',
                cancelButtonText: 'Hủy'
            }).then(async (result) => {
                if (result.isConfirmed) {
                    try {
                        const response = await fetch('/admin/reviews/delete-multiple', {
                            method: 'DELETE',
                            headers: {
                                'Content-Type': 'application/json',
                            },
                            body: JSON.stringify({ reviewIds: selectedReviews }),
                        });

                        if (response.ok) {
                            Swal.fire('Đã xóa!', 'Các đánh giá đã được xóa thành công.', 'success').then(() => {
                                location.reload();
                            });
                        } else {
                            Swal.fire('Lỗi', 'Không thể xóa các đánh giá này.', 'error');
                        }
                    } catch (error) {
                        Swal.fire('Lỗi', 'Không thể xóa các đánh giá này.', 'error');
                    }
                }
            });
        } else {
            Swal.fire('Chưa chọn', 'Vui lòng chọn ít nhất một đánh giá.', 'warning');
        }
    }

    // Hiển thị phần trả lời khi nhấn vào nút "Trả lời"
    function showReplyInput(reviewId) {
        const replySection = document.getElementById('replySection-' + reviewId);
        replySection.style.display = (replySection.style.display === 'none' || replySection.style.display === '') ? 'block' : 'none';
    }

    // Gửi phản hồi sau khi nhập
    function submitReply(reviewId) {
        const replyContent = document.getElementById('replyContent-' + reviewId).value;

        if (replyContent.trim() === '') {
            alert('Vui lòng nhập phản hồi!');
            return;
        }

        // Gửi phản hồi qua AJAX hoặc gửi form (ở đây dùng fetch để gửi AJAX)
        fetch('/admin/reviews/reply/' + reviewId, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ content: replyContent })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Phản hồi đã được gửi thành công!');
                document.getElementById('replySection-' + reviewId).style.display = 'none';
            } else {
                alert('Có lỗi xảy ra, vui lòng thử lại!');
            }
        })
        .catch(error => {
            alert('Có lỗi xảy ra: ' + error);
        });
    }

    // Tạo sự kiện cho nút xóa nhiều đánh giá
    const deleteButton = document.getElementById('deleteReviews');
    if (deleteButton) {
        deleteButton.addEventListener('click', confirmDeleteMultiple);
    }
});
