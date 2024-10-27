package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.Discount;
import yourstyle.com.shope.repository.DiscountRepository;
import yourstyle.com.shope.service.DiscountService;

@Service
public class DiscountServiceImpl implements DiscountService{

    @Autowired
	DiscountRepository discountrepository;

	@Override
    public Discount update(Discount discount) {
        return discountrepository.save(discount);
    }

	@Override
	public <S extends Discount> S save(S entity) {
		return discountrepository.save(entity);
	}

	@Override
	public <S extends Discount> Optional<S> findOne(Example<S> example) {
		return discountrepository.findOne(example);
	}

	@Override
    public Page<Discount> findAll(Pageable pageable) {
        return discountrepository.findAll(pageable);
    }

	@Override
	public List<Discount> findAll(Sort sort) {
		return discountrepository.findAll(sort);
	}

	@Override
	public List<Discount> findAll() {
		return discountrepository.findAll();
	}

	@Override
	public Optional<Discount> findById(Integer id) {
		return discountrepository.findById(id);
	}

	@Override
	public long count() {
		return discountrepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		discountrepository.deleteById(id);
	}

	@Override
    public List<Discount> findByProductId(Integer productId) {
        return discountrepository.findByProduct_ProductId(productId);
    }
}
