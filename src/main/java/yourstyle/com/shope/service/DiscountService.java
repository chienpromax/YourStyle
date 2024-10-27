package yourstyle.com.shope.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import yourstyle.com.shope.model.Discount;

public interface DiscountService {

    void deleteById(Integer id);

    long count();

    Optional<Discount> findById(Integer id);

    List<Discount> findAll();

    Page<Discount> findAll(Pageable pageable);

    List<Discount> findAll(Sort sort);

    <S extends Discount> Optional<S> findOne(Example<S> example);

    <S extends Discount> S save(S entity);

    Discount update(Discount discount);

    List<Discount> findByProductId(Integer productId);

}
