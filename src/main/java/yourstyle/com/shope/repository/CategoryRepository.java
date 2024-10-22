package yourstyle.com.shope.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{
        
        List<Category> findByParentCategoryNotNull();
}
