package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

import yourstyle.com.shope.model.Category;

public interface CategoryService {
    List<Category> findParentCategories();
    List<Category> findChildCategories(Integer parentId);

    List<Category> getChildCategories();
    long count();

    <S extends Category> long count(Example<S> example);

    void delete(Category entity);

    void deleteAll();

    void deleteAll(Iterable<? extends Category> entities);

    void deleteAllById(Iterable<? extends Integer> ids);

    void deleteAllByIdInBatch(Iterable<Integer> ids);

    void deleteAllInBatch();

    void deleteAllInBatch(Iterable<Category> entities);

    void deleteById(Integer id);

    void deleteInBatch(Iterable<Category> entities);

    <S extends Category> boolean exists(Example<S> example);

    boolean existsById(Integer id);

    <S extends Category> List<S> findAll(Example<S> example);

    <S extends Category> List<S> findAll(Example<S> example, Sort sort);

    List<Category> findAll();

    List<Category> findAll(Sort sort);

    Page<Category> findAll(Pageable pageable);

    <S extends Category> Page<S> findAll(Example<S> example, Pageable pageable);

    List<Category> findAllById(Iterable<Integer> ids);

    <S extends Category, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction);

    Optional<Category> findById(Integer id);

    <S extends Category> Optional<S> findOne(Example<S> example);

    void flush();

    Category getById(Integer arg0);

    Category getOne(Integer arg0);

    Category getReferenceById(Integer id);

    <S extends Category> S save(S entity);

    <S extends Category> List<S> saveAll(Iterable<S> entities);

    <S extends Category> List<S> saveAllAndFlush(Iterable<S> entities);

    <S extends Category> S saveAndFlush(S entity);

    //
    List<Category> findByParentCategoryIsNull();
}
