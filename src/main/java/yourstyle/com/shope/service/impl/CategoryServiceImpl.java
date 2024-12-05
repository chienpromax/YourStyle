package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.repository.CategoryRepository;
import yourstyle.com.shope.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // @Override
    // public List<Category> findParentCategories() {
    // List<Category> parentCategories =
    // categoryRepository.findByParentCategoryIsNull();
    // for (Category parent : parentCategories) {
    // parent.setChildCategories(categoryRepository.findByParentCategory(parent));
    // }
    // return parentCategories;
    // }

    @Override
    public List<Category> findChildCategories(Integer parentId) {
        return categoryRepository.findByParentCategoryNotNull()
                .stream()
                .filter(category -> category.getParentCategory() != null &&
                        category.getParentCategory().getCategoryId().equals(parentId))
                .toList();
    }

    @Override
    public List<Category> getChildCategories() {
        return categoryRepository.findByParentCategoryNotNull();
    }

    // @Override
    // public List<Category> getParentCategories() {
    // return categoryRepository.findByParentCategoryIsNull(); // Chỉ lấy các danh
    // mục cha
    // }

    @Override
    public long count() {
        return categoryRepository.count();
    }

    @Override
    public <S extends Category> long count(Example<S> example) {
        return categoryRepository.count(example);
    }

    @Override
    public void delete(Category entity) {
        categoryRepository.delete(entity);
    }

    @Override
    public void deleteAll() {
        categoryRepository.deleteAll();
    }

    @Override
    public void deleteAll(Iterable<? extends Category> entities) {
        categoryRepository.deleteAll(entities);
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
        categoryRepository.deleteAllById(ids);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> ids) {
        categoryRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public void deleteAllInBatch() {
        categoryRepository.deleteAllInBatch();
    }

    @Override
    public void deleteAllInBatch(Iterable<Category> entities) {
        categoryRepository.deleteAllInBatch(entities);
    }

    @Override
    public void deleteById(Integer id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public void deleteInBatch(Iterable<Category> entities) {
        categoryRepository.deleteInBatch(entities);
    }

    @Override
    public <S extends Category> boolean exists(Example<S> example) {
        return categoryRepository.exists(example);
    }

    @Override
    public boolean existsById(Integer id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public <S extends Category> List<S> findAll(Example<S> example) {
        return categoryRepository.findAll(example);
    }

    @Override
    public <S extends Category> List<S> findAll(Example<S> example, Sort sort) {
        return categoryRepository.findAll(example, sort);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> findAll(Sort sort) {
        return categoryRepository.findAll(sort);
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public <S extends Category> Page<S> findAll(Example<S> example, Pageable pageable) {
        return categoryRepository.findAll(example, pageable);
    }

    @Override
    public List<Category> findAllById(Iterable<Integer> ids) {
        return categoryRepository.findAllById(ids);
    }

    @Override
    public <S extends Category, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        return categoryRepository.findBy(example, queryFunction);
    }

    @Override
    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }

    @Override
    public <S extends Category> Optional<S> findOne(Example<S> example) {
        return categoryRepository.findOne(example);
    }

    @Override
    public void flush() {
        categoryRepository.flush();
    }

    @Override
    public Category getById(Integer arg0) {
        return categoryRepository.getById(arg0);
    }

    @Override
    public Category getOne(Integer arg0) {
        return categoryRepository.getOne(arg0);
    }

    @Override
    public Category getReferenceById(Integer id) {
        return categoryRepository.getReferenceById(id);
    }

    @Override
    public <S extends Category> S save(S entity) {
        return categoryRepository.save(entity);
    }

    @Override
    public <S extends Category> List<S> saveAll(Iterable<S> entities) {
        return categoryRepository.saveAll(entities);
    }

    @Override
    public <S extends Category> List<S> saveAllAndFlush(Iterable<S> entities) {
        return categoryRepository.saveAllAndFlush(entities);
    }

    @Override
    public <S extends Category> S saveAndFlush(S entity) {
        return categoryRepository.saveAndFlush(entity);
    }

    @Override
    public List<Category> findByParentCategoryIsNull() {
        return categoryRepository.findByParentCategoryIsNull();
    }

    @Override
    public List<Category> findParentCategories() {
        List<Category> parentCategories = categoryRepository.findByParentCategoryIsNull()
                .stream()
                .filter(Category::getStatus) 
                .collect(Collectors.toList());

        for (Category parent : parentCategories) {
            List<Category> activeChildCategories = categoryRepository.findByParentCategory(parent)
                    .stream()
                    .filter(Category::getStatus)
                    .collect(Collectors.toList());
            parent.setChildCategories(activeChildCategories);
        }

        return parentCategories;
    }

}
