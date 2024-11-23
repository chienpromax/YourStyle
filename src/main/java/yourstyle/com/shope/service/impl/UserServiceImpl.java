package yourstyle.com.shope.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.User;
import yourstyle.com.shope.repository.AccountRepository;
import yourstyle.com.shope.repository.UserRepository;
import yourstyle.com.shope.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AccountRepository accountRepository;

	@Override
	public Map<String, Object> getUserProfile(String username) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			return Map.of("username", user.getUsername(), "dateOfBirth", user.getDateOfBirth(), "gender",
					user.getGender(), "nationality", user.getNationality(), "phoneNumber", user.getPhoneNumber(),
					"avatar", user.getAvatar() // Đảm bảo có thêm avatar nếu có
			);
		}
		return null; // Trả về null nếu người dùng chưa có thông tin
	}

	@Override
	public boolean updateProfile(String username, String dateOfBirth, String gender, String nationality) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			user.setDateOfBirth(dateOfBirth);
			user.setGender(gender);
			user.setNationality(nationality);
			userRepository.save(user); // Lưu lại thông tin người dùng
			return true;
		}
		return false;
	}

	@Override
	public boolean userExists(String username) {
		return userRepository.existsByUsername(username); // Kiểm tra xem người dùng đã tồn tại chưa
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll(); // Lấy tất cả người dùng từ cơ sở dữ liệu
	}

	@Override
	public boolean deleteUser(String username) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			userRepository.delete(user); // Xóa người dùng
			return true;
		}
		return false; // Trả về false nếu không tìm thấy người dùng
	}

	@Override
	public boolean updateAvatar(String username, String imageUrl) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			user.setAvatar(imageUrl); // Cập nhật avatar của người dùng
			userRepository.save(user);
			return true;
		}
		return false;
	}

	// Triển khai phương thức getUserById
	@Override
	public User getUserById(int id) {
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public String getEmailByUsername(String username) {
		Optional<Account> accountOptional = accountRepository.findByUsername(username);
		if (accountOptional.isPresent()) {
			return accountOptional.get().getEmail(); // Lấy email từ đối tượng Account
		}
		return null; 
	}

	@Override
	public Integer getAccountIdByUsername(String username) {
	    Optional<Account> accountOptional = accountRepository.findByUsername(username);
	    if (accountOptional.isPresent()) {
	        return accountOptional.get().getAccountId(); // Lấy accountId từ đối tượng Account
	    }
	    return null; // Trả về null nếu không tìm thấy tài khoản
	}

	

	
	public boolean updatePhoneNumber(Integer userId, String phone) {
	    // Tìm người dùng theo userId
	    User user = userRepository.findById(userId).orElse(null);
	    if (user == null) {
	        return false;
	    }
	    user.setPhoneNumber(phone);

	    userRepository.save(user);

	    return true;
	}
	
	@Override
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

}
