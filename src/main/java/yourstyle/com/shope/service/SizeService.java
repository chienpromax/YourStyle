package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import yourstyle.com.shope.model.Size;


public interface SizeService {

    void deleteById(Integer id);

    long count();

    Optional<Size> findById(Integer id);

    List<Size> findAll();

    Page<Size> findAll(Pageable pageable);

    List<Size> findAll(Sort sort);


    <S extends Size> Optional<S> findOne(Example<S> example);

    <S extends Size> S save(S entity);

    Size update(Size size);
    
}
