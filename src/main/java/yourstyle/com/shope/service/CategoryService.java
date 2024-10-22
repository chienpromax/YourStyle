package yourstyle.com.shope.service;

import java.util.List;

import yourstyle.com.shope.model.Category;

public interface CategoryService {

    List<Category> findAll();

    List<Category> getChildCategories();

}
