// function addToFavorite(productId) {
// 		if (productId != null) {
// 			window.location.href=`/yourstyle/productfavorites/add?productId=${productId}`;

// 		}
// 	}
// 	 $(document).ready(function() {
//          $('#addFavoriteBtn').click(function() {
//              var productId = $(this).data('product-id');
             
//              $.ajax({
//                  url: '/add', // Đường dẫn tới phương thức addFavorite
//                  type: 'GET',
//                  data: { productId: productId },
//                  success: function(response) {
//                      $('#messageArea').text(response);
//                  },
//                  error: function(xhr) {
//                      $('#messageArea').text(xhr.responseText);
//                  }
//              });
//          });
//      });