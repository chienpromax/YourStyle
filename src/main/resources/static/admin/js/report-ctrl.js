app.controller('reportCtrl', function ($scope, $http) {
    $scope.selectedCusTime = 'today';
    $scope.customerStats = {
        today: 0,
        thisMonth: 0,
        thisYear: 0
    };

    $scope.selectedPVTime = 'today';
    $scope.productVariantStats = {
        today: 0,
        thisMonth: 0,
        thisYear: 0
    };

    $scope.selectedSalesTime = 'today';
    $scope.salesStats = {
        today: 0,
        thisMonth: 0,
        thisYear: 0
    };

    $scope.selectedRevenueTime = 'today';
    $scope.revenueStats = {
        today: 0,
        thisWeek: 0,
        thisMonth: 0,
        thisQuarter: 0,
        thisYear: 0
    };

    // Gọi API cho số lượng khách hàng
    $http.get('/rest/reports/customers/today').then(function (response) {
        $scope.customerStats.today = response.data;
    });
    $http.get('/rest/reports/customers/thisMonth').then(function (response) {
        $scope.customerStats.thisMonth = response.data;
    });
    $http.get('/rest/reports/customers/thisYear').then(function (response) {
        $scope.customerStats.thisYear = response.data;
    });

    // Gọi API cho số lượng sản phẩm
    $http.get('/rest/reports/productVariants/today').then(function (response) {
        $scope.productVariantStats.today = response.data;
    });
    $http.get('/rest/reports/productVariants/thisMonth').then(function (response) {
        $scope.productVariantStats.thisMonth = response.data;
    });
    $http.get('/rest/reports/productVariants/thisYear').then(function (response) {
        $scope.productVariantStats.thisYear = response.data;
    });

    // Gọi API cho thống kê sản phẩm bán được
    $http.get('/rest/reports/sales/today').then(function (response) {
        $scope.salesStats.today = response.data;
    });
    $http.get('/rest/reports/sales/thisMonth').then(function (response) {
        $scope.salesStats.thisMonth = response.data;
    });
    $http.get('/rest/reports/sales/thisYear').then(function (response) {
        $scope.salesStats.thisYear = response.data;
    });

    // Gọi API cho thống kê doanh thu
    $http.get('/rest/reports/revenue/today').then(function (response) {
        $scope.revenueStats.today = response.data;
    });
    $http.get('/rest/reports/revenue/thisWeek').then(function (response) {
        $scope.revenueStats.thisWeek = response.data;
    });
    $http.get('/rest/reports/revenue/thisMonth').then(function (response) {
        $scope.revenueStats.thisMonth = response.data;
    });
    $http.get('/rest/reports/revenue/thisQuarter').then(function (response) {
        $scope.revenueStats.thisQuarter = response.data;
    });
    $http.get('/rest/reports/revenue/thisYear').then(function (response) {
        $scope.revenueStats.thisYear = response.data;
    });


    $scope.selectedCustomerTime = 'today';
    $scope.customers = [];

    function loadTopCustomers() {
        let url = '/rest/reports/topSpending/' + $scope.selectedCustomerTime;
        $http.get(url).then(function (response) {
            $scope.customers = response.data;
        });
    }
    $scope.$watch('selectedCustomerTime', function () {
        loadTopCustomers();
    });

    $scope.selectedProductTime = 'today';
    $scope.topProducts = [];

    function loadTopSellingProducts() {
        let url = '/rest/reports/topSelling/' + $scope.selectedProductTime;
        $http.get(url).then(function (response) {
            $scope.topProducts = response.data;
        });
    }

    $scope.$watch('selectedProductTime', function () {
        loadTopSellingProducts();
    });

    //San pham khong ban chay
    $scope.productWithoutOrders = [];
    $scope.currentPage = 0;
    $scope.pageSize = 3;

    function loadProductWithoutOrders(page = 0) {
        let url = `/rest/reports/product/withoutOrders?page=${page}&size=${$scope.pageSize}`;
        $http.get(url).then(function (response) {
            $scope.productWithoutOrders = response.data.content;
            $scope.totalPages = response.data.totalPages;
        }, function (error) {
            console.error('Lỗi khi tải sản phẩm không có đơn hàng: ', error);
        });
    }
    $scope.changePage = function (page) {
        if (page >= 0 && page < $scope.totalPages) {
            $scope.currentPage = page;
            loadProductWithoutOrders(page);
        }
    };
    loadProductWithoutOrders();

    // Sản phẩm sắp hết hàng
    $scope.lowStockProducts = [];
    $scope.lowStockProductsCurrentPage = 0;
    $scope.lowStockProductsPageSize = 10;
    $scope.lowStockProductsTotalPages = 0;
    $scope.lowStockThreshold = 20;

    function loadLowStockProducts(page = 0) {
        let url = `/rest/reports/product/lowStock?threshold=${$scope.lowStockThreshold}&page=${page}&size=${$scope.lowStockProductsPageSize}`;
        $http.get(url).then(function (response) {
            $scope.lowStockProducts = response.data.content;
            $scope.lowStockProductsTotalPages = response.data.totalPages;
        }, function (error) {
            console.error('Lỗi khi tải sản phẩm sắp hết hàng: ', error);
        });
    }

    $scope.changeLowStockProductsPage = function (page) {
        if (page >= 0 && page < $scope.lowStockProductsTotalPages) {
            $scope.lowStockProductsCurrentPage = page;
            loadLowStockProducts(page);
        }
    };

    // Gọi hàm ban đầu
    loadLowStockProducts($scope.lowStockProductsCurrentPage);

});

app.filter('currencyVND', function () {
    return function (input) {
        if (input === null || input === undefined) return '';

        input = parseFloat(input);

        input = Math.round(input / 1000) * 1000;

        return input
            .toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })
            .replace('₫', '') + '₫';
    };
});
