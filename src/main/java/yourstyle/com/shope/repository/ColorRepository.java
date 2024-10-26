package yourstyle.com.shope.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Color;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer>{
    Page<Color> findByColorNameContaining(String name, Pageable pageable);
}
