// Hiển thị dropdown lịch sử khi nhấp vào ô tìm kiếm
function showDropdown() {
    document.getElementById('searchHistoryDropdown').style.display = 'block';
    document.getElementById('suggestedProductsDropdown').style.display = 'none'; // Ẩn gợi ý khi nhấn vào ô tìm kiếm
}

// Ẩn dropdown lịch sử và hiển thị dropdown gợi ý sản phẩm khi nhập từ khóa
// Cập nhật hàm để gọi API và lưu lịch sử tìm kiếm
function fetchProductSuggestions(query) {
    if (!query) {
        // Nếu không có từ khóa, hiển thị lại lịch sử tìm kiếm
        document.getElementById('suggestedProductsDropdown').style.display = 'none';
        document.getElementById('searchHistoryDropdown').style.display = 'block';
        return;
    }

    // Ẩn lịch sử tìm kiếm và hiển thị gợi ý sản phẩm
    document.getElementById('searchHistoryDropdown').style.display = 'none';
    document.getElementById('suggestedProductsDropdown').style.display = 'block';

    fetch(`/productSuggestions?name=${encodeURIComponent(query)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            const suggestionsDropdown = document.getElementById('suggestedProductsDropdown');
            suggestionsDropdown.innerHTML = data.map(product => `
                <li>
                    <a href="/yourstyle/product/detail/${product.productId}" 
                       onclick="addToSearchHistory('${product.name}')">
                       ${product.name}
                    </a>
                </li>
            `).join('');
        })
        .catch(error => {
            console.error('Fetch error:', error);
            document.getElementById('suggestedProductsDropdown').style.display = 'none';
        });
}

// Thêm vào lịch sử tìm kiếm khi nhấn vào gợi ý
function addToSearchHistory(keyword) {
    fetch(`/addSearchHistory?keyword=${encodeURIComponent(keyword)}`, {
        method: 'POST',
    })
        .then(response => {
            if (!response.ok) {
                console.error('Failed to add to search history');
            }
        })
        .catch(error => {
            console.error('Error adding to search history:', error);
        });
}


// Ẩn dropdown khi nhấp ra ngoài ô tìm kiếm
document.addEventListener("click", function (event) {
    const historyDropdown = document.getElementById('searchHistoryDropdown');
    const suggestionsDropdown = document.getElementById('suggestedProductsDropdown');
    const input = document.querySelector("input[name='name']");

    if (!input.contains(event.target) && !historyDropdown.contains(event.target) && !suggestionsDropdown.contains(event.target)) {
        historyDropdown.style.display = 'none';
        suggestionsDropdown.style.display = 'none';
    }
});

// Lấy dữ liệu lịch sử tìm kiếm khi tải trang
document.addEventListener("DOMContentLoaded", function () {
    fetch('/searchHistory')
        .then(response => response.json())
        .then(data => {
            console.log(data);
            const dropdown = document.getElementById('searchHistoryDropdown');
            dropdown.innerHTML = data.map(item => `
                <li id="history-item-${item.searchId}">
                    <i class="fas fa-history pe-3"></i>
                    <a href="/search?name=${encodeURIComponent(item.keyword)}">${item.keyword}</a>
                    <span onclick="deleteSearchHistory(${item.searchId})">X</span>
                </li>
            `).join('');
        });
});

//Xóa lịch sử tìm kiếm
function deleteSearchHistory(id) {
    fetch(`/deleteSearchHistory?id=${id}`)
        .then(response => {
            if (response.ok) {
                document.getElementById(`history-item-${id}`).remove()
            } else {
                console.error('Failed to delete search history')
            }
        })
};
