package yourstyle.com.shope.service;

import yourstyle.com.shope.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

	Map<String, Object> getUserProfile(String username);

	boolean updateProfile(String username, String dateOfBirth, String gender, String nationality);

	boolean userExists(String username);

	List<User> getAllUsers();

	boolean deleteUser(String username);

	boolean updateAvatar(String username, String imageUrl);

	// Thêm phương thức này
	User getUserById(int id);

	User getUserByUsername(String username);

	String getEmailByUsername(String username);

	Integer getAccountIdByUsername(String username);
	
	Optional<User> findById(int id);

    void save(User user);
    
}
