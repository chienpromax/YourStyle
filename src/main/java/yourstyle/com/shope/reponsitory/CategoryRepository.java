package yourstyle.com.shope.reponsitory;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import yourstyle.com.shope.model.Category;
import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository <Category, Integer> {
    Page<Category> findAll(Pageable pageable);
    boolean existsByName(String name);
    boolean existsByNameAndCategoryIdNot(String name, Integer id);
    List<Category> findByParentCategoryIsNull();
}

