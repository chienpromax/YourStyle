package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Example;

import yourstyle.com.shope.model.Product;

public interface ProductService {

    void deleteById(Integer id);

    long count();

    Optional<Product> findById(Integer id);

    List<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    List<Product> findAll(Sort sort);

    <S extends Product> Optional<S> findOne(Example<S> example);

    <S extends Product> S save(S entity);

    Product update(Product product);

    Page<Product> searchByName(String name, Pageable pageable);

}
