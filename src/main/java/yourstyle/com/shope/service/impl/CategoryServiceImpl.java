package yourstyle.com.shope.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.repository.CategoryRepository;
import yourstyle.com.shope.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    CategoryRepository categoryRepository;
    
    @Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

    @Override
    public List<Category> getChildCategories() {
        return categoryRepository.findByParentCategoryNotNull();
    }
    
}
