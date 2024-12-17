package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.Color;
import yourstyle.com.shope.repository.ColorRepository;
import yourstyle.com.shope.repository.ProductVariantRepository;
import yourstyle.com.shope.service.ColorService;

@Service
public class ColorServiceImpl implements ColorService {

    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    
    public boolean isColorInUse(Integer colorId) {
        boolean exists = productVariantRepository.existsByColor_ColorId(colorId);
        return exists;
    }    

    @Override
    public void deleteById(Integer colorId) {
        if (isColorInUse(colorId)) {
            throw new IllegalArgumentException("Màu đang được sử dụng, không thể xóa!");
        }
        // Nếu không có sản phẩm nào sử dụng màu này, tiến hành xóa màu
        colorRepository.deleteById(colorId);
    }
    
    @Override
    public long count() {
        return colorRepository.count();
    }

    @Override
    public Optional<Color> findById(Integer id) {
        return colorRepository.findById(id);
    }

    @Override
    public List<Color> findAll() {
        return colorRepository.findAll();
    }

    @Override
    public Page<Color> findAll(Pageable pageable) {
        return colorRepository.findAll(pageable);
    }

    @Override
    public List<Color> findAll(Sort sort) {
        return colorRepository.findAll(sort);
    }

    @Override
    public <S extends Color> Optional<S> findOne(Example<S> example) {
        return colorRepository.findOne(example);
    }

    @Override
    public <S extends Color> S save(S entity) {
        return colorRepository.save(entity);
    }
}