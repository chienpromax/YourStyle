package yourstyle.com.shope.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yourstyle.com.shope.model.Slide;

@Repository
public interface SlideRepository extends JpaRepository<Slide, Integer> {

}
