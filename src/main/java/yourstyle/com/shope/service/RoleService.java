package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import yourstyle.com.shope.model.Role;

public interface RoleService {

	void deleteById(Integer id);

	long count();

	Optional<Role> findById(Integer id);

	List<Role> findAll();

	Page<Role> findAll(Pageable pageable);

	List<Role> findAll(Sort sort);

	<S extends Role> S save(S entity);

}
