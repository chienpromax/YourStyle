package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.OrderDetail;
import yourstyle.com.shope.repository.OrderDetailRepository;
import yourstyle.com.shope.service.OrderDetailService;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {
	@Autowired
	OrderDetailRepository orderDetailRepository;
	
	public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository) {
		this.orderDetailRepository = orderDetailRepository;
	}

	@Override
	public <S extends OrderDetail> S save(S entity) {
		return orderDetailRepository.save(entity);
	}

	@Override
	public List<OrderDetail> findAll(Sort sort) {
		return orderDetailRepository.findAll(sort);
	}

	@Override
	public Page<OrderDetail> findAll(Pageable pageable) {
		return orderDetailRepository.findAll(pageable);
	}

	@Override
	public List<OrderDetail> findAll() {
		return orderDetailRepository.findAll();
	}

	@Override
	public Optional<OrderDetail> findById(Integer id) {
		return orderDetailRepository.findById(id);
	}

	@Override
	public void deleteById(Integer id) {
		orderDetailRepository.deleteById(id);
	}
	
	
	
}
