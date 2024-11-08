package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.OrderStatusHistory;
import yourstyle.com.shope.repository.OrderStatusHistoryRepository;
import yourstyle.com.shope.service.OrderStatusHistoryService;

@Service
public class OrderStatusHistoryServiceImpl implements OrderStatusHistoryService {
	@Autowired
	OrderStatusHistoryRepository orderStatusHistoryRepository;

	public OrderStatusHistoryServiceImpl(OrderStatusHistoryRepository orderStatusHistoryRepository) {
		this.orderStatusHistoryRepository = orderStatusHistoryRepository;
	}

	@Override
	public List<OrderStatusHistory> findByOrderOrderId(Integer orderId) {
		return orderStatusHistoryRepository.findByOrderOrderId(orderId);
	}

	@Override
	public <S extends OrderStatusHistory> S save(S entity) {
		return orderStatusHistoryRepository.save(entity);
	}

	@Override
	public List<OrderStatusHistory> findAll() {
		return orderStatusHistoryRepository.findAll();
	}

	@Override
	public Optional<OrderStatusHistory> findById(Integer id) {
		return orderStatusHistoryRepository.findById(id);
	}

	@Override
	public void deleteById(Integer id) {
		orderStatusHistoryRepository.deleteById(id);
	}

}
