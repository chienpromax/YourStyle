app.controller('comment-ctrl', function ($scope, $http, $location) {

    const productId = $location.absUrl().split('/').pop(); // Lấy giá trị productId từ URL

    $scope.reviews = [];
    $scope.currentPage = 0;
    $scope.totalPages = 0;
    $scope.newComment = '';
    $scope.rating = 0;

    $scope.customerNow;

    $http.get('/reviews/getCustomer').then(function (response) {
        console.log(response.data);
        $scope.customerNow = response.data;
    });

    $scope.loadReviews = function (page) {
        $http.get('/reviews/' + productId + '?page=' + page)
            .then(function (response) {
                $scope.reviews = response.data.content; // Gán dữ liệu đánh giá
                $scope.currentPage = response.data.number;
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
        const commentData = {
            productId: productId,
            comment: $scope.newComment,
            rating: $scope.rating
        };

        // Gửi yêu cầu POST để thêm đánh giá
        $http.post('/reviews/' + productId, commentData)
            .then(function (response) {
                // Sau khi thành công, tải lại danh sách đánh giá
                $scope.loadReviews($scope.currentPage);
                $scope.newComment = '';  // Reset input sau khi gửi
                $scope.rating = 0;
            }, function (error) {
                console.error('Error submitting review:', error);
            });
    };

    $scope.resetForm = function () {
        // Reset comment và rating
        $scope.newComment = '';  // Xóa nội dung bình luận
        $scope.rating = 0;       // Reset rating về 0
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
