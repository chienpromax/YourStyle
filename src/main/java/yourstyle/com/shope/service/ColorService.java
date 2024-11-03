package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import yourstyle.com.shope.model.Color;

public interface ColorService {

    void deleteById(Integer id);

    long count();

    Optional<Color> findById(Integer id);

    List<Color> findAll();

    Page<Color> findAll(Pageable pageable);

    List<Color> findAll(Sort sort);

    <S extends Color> Optional<S> findOne(Example<S> example);

    <S extends Color> S save(S entity);

}
