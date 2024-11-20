package yourstyle.com.shope.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import yourstyle.com.shope.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer>{
	Optional<Role> findByName(String name); // Tìm kiếm role theo tên vai trò.
}
