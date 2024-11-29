app.controller('comment-ctrl', function ($scope, $http, $location) {

    // Lấy productId từ URL
    const productId = $location.absUrl().split('/').pop();

    $scope.reviews = [];
    $scope.currentPage = 0;
    $scope.totalPages = 0;
    $scope.newComment = '';
    $scope.rating = 0;

    $scope.customerNow;

    $http.get('/reviews/getCustomer').then(function (response) {
        $scope.customerNow = response.data;
    });

    $scope.loadReviews = function (page) {
        $http.get('/reviews/' + productId + '?page=' + page)
            .then(function (response) {
                $scope.reviews = response.data.content; // Gán dữ liệu đánh giá
                $scope.currentPage = response.data.currentPage;
                $scope.totalPages = response.data.totalPages;

                // Tạo mảng các trang để lặp qua
                $scope.pageNumbers = Array.from({ length: $scope.totalPages }, (v, k) => k);
            }, function (error) {
                console.error('Error fetching reviews:', error);
            });
    };

    // Hàm để cập nhật rating khi người dùng chọn sao
    $scope.setRating = function (ratingValue) {
        $scope.rating = ratingValue;
    };

    // Hàm gửi đánh giá mới lên server
    $scope.submitComment = function () {
        if (!$scope.newComment || $scope.rating === 0) {
            alert("Vui lòng nhập bình luận và chọn sao.");
            return;
        }

        const formData = new FormData();
        const review = {
            comment: $scope.newComment,
            rating: $scope.rating
        };

        formData.append('review', JSON.stringify(review));

        // Gắn ảnh nếu có
        if ($scope.images && $scope.images.length > 0) {
            for (let i = 0; i < $scope.images.length; i++) {
                formData.append('images', $scope.images[i]);
            }
        }

        // Gửi yêu cầu POST để thêm đánh giá
        $http.post('/reviews/' + productId, formData, {
            transformRequest: angular.identity,
            headers: { 'Content-Type': undefined }
        }).then(function (response) {
            $scope.loadReviews($scope.currentPage);
            $scope.newComment = '';
            $scope.rating = 0;
            $scope.images = [];
            document.getElementById('imagesInput').value = null;
        }, function (error) {
            console.error('Error submitting review:', error);
        });
    };

    $scope.images = [];
    $scope.previewImages = [];

    $scope.handleFileUpload = function (files) {
        $scope.images = [];
        $scope.previewImages = [];
        if (files.length > 4) {
            alert("Bạn chỉ có thể tải tối đa 4 ảnh.");
            return;
        }
        for (let i = 0; i < files.length; i++) {
            const file = files[i];
            const reader = new FileReader();
            reader.onload = function (e) {
                $scope.$apply(function () {
                    $scope.previewImages.push(e.target.result); // Lưu base64 ảnh vào danh sách
                    $scope.images.push(file); // Lưu file để gửi lên server
                });
            };
            reader.readAsDataURL(file); // Đọc file
        }
    };


    $scope.resetForm = function () {
        $scope.newComment = '';
        $scope.rating = 0;
        $scope.images = [];
        document.getElementById('imagesInput').value = null;
    };

    // Gọi hàm tải dữ liệu lần đầu
    $scope.loadReviews($scope.currentPage);

    $scope.deleteReview = function (reviewId) {
        if (confirm("Bạn có chắc chắn muốn xóa đánh giá này không?")) {
            $http.delete('/reviews/' + reviewId)
                .then(function (response) {
                    // Tải lại danh sách đánh giá sau khi xóa
                    $scope.loadReviews($scope.currentPage);
                }, function (error) {
                    console.error('Error deleting review:', error);
                });
        }
    };
});
