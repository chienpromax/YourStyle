package yourstyle.com.shope.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.model.ProductFavorite;
import yourstyle.com.shope.repository.ProductFavoriteRepository;
import yourstyle.com.shope.repository.ProductRepository;
	@Service
	public class ProductFavoriteService {

	    @Autowired
	    private ProductFavoriteRepository productFavoriteRepository;

	    @Autowired
	    private ProductRepository productRepository;

	    public ArrayList<Optional<Product>> getFavoriteProductsByCustomerId(int customerId) {

	        List<ProductFavorite> favoriteProducts = productFavoriteRepository.findProductFavoritesByCustomerId(customerId);  //findProductsByCustomerId(customerId);
			ArrayList<Optional<Product>> listProductUser = new ArrayList<Optional<Product>>();
			
			for (ProductFavorite fProducts : favoriteProducts) {
				Optional<Product> productFound = productRepository.findById(fProducts.getProductId());
					if(productFound != null) listProductUser.add(productFound);  
			}
			
			

			return listProductUser;
	    }
		
	    public boolean addProductToFavorite(int customerId, int productId) {	
	        if (!productFavoriteRepository.existsByCustomerIdAndProductId(customerId, productId)) {
	            ProductFavorite favorite = new ProductFavorite();
	            favorite.setCustomerId(customerId);
	            favorite.setProductId(productId);
	            favorite.setTimeAt(new Timestamp(System.currentTimeMillis()));
	            try {
	                productFavoriteRepository.save(favorite);
	                return true;
	            } catch (Exception e) {
	                System.err.println("Lỗi khi thêm sản phẩm vào danh sách yêu thích: " + e.getMessage());
	                e.printStackTrace(); 
	                return false;
	            }

	        }
	        return false; 
	    }


	    public boolean removeProductFromFavorite(int customerId, int productId) {
	        var favorite = productFavoriteRepository.findByCustomerIdAndProductId(customerId, productId);
	        if (favorite.isPresent()) {
	            productFavoriteRepository.delete(favorite.get()); // Xóa sản phẩm yêu thích
	            return true; 
	        }
	        return false; 
	    }

	}
