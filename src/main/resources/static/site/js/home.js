function fetchProductSuggestions(query) {
    const historyDropdown = document.getElementById('searchHistoryDropdown');
    const suggestionsDropdown = document.getElementById('suggestedProductsDropdown');

    if (query.length > 0) {
        historyDropdown.style.display = 'none';
        suggestionsDropdown.style.display = 'block';

        // Gửi yêu cầu tìm kiếm gợi ý sản phẩm qua AJAX
        fetch(`/productSuggestions?name=${encodeURIComponent(query)}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                console.log(data);
                suggestionsDropdown.innerHTML = data.map(item => `
        <li><a href="/search?name=${encodeURIComponent(item.name)}">${item.name}</a></li>
    `).join('');
            })
            .catch(error => {
                console.error('Fetch error:', error);
            });
    } else {
        historyDropdown.style.display = 'block';
        suggestionsDropdown.style.display = 'none';
    }
}

function addToFavorite(productId) {
    if (productId != null) {
        window.location.href = `/yourstyle/productfavorites/add?productId=${productId}`;

    }
}
$(document).ready(function () {
    $('#addFavoriteBtn').click(function () {
        var productId = $(this).data('product-id');

        $.ajax({
            url: '/add', // Đường dẫn tới phương thức addFavorite
            type: 'GET',
            data: {
                productId: productId
            },
            success: function (response) {
                $('#messageArea').text(response);
            },
            error: function (xhr) {
                $('#messageArea').text(xhr.responseText);
            }
        });
    });
});