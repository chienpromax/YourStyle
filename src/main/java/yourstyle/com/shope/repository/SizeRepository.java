package yourstyle.com.shope.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer>{

    Page<Size> findBySizeNameContaining(String name, Pageable pageable);
    
}
