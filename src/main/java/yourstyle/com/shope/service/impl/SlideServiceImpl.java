package yourstyle.com.shope.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yourstyle.com.shope.model.Slide;
import yourstyle.com.shope.repository.SlideRepository;
import yourstyle.com.shope.service.SlideService;

@Service
public class SlideServiceImpl implements SlideService {

    @Autowired
    private SlideRepository slideRepository;

    @Override
    public void addSlide(Slide slide) {
        slideRepository.save(slide);  // Lưu slide mới vào cơ sở dữ liệu
    }

    @Override
    public void editSlide(Slide slide) {
        slideRepository.save(slide);  // Cập nhật slide vào cơ sở dữ liệu
    }

    @Override
    public void deleteSlide(int slideId) {
        slideRepository.deleteById(slideId);  // Xóa slide theo ID
    }

    @Override
    public List<Slide> getAllSlides() {
        return slideRepository.findAll();  // Lấy danh sách tất cả slide
    }

    @Override
    public Slide getSlideById(int slideId) {
        Optional<Slide> slide = slideRepository.findById(slideId);  // Tìm slide theo ID
        return slide.orElse(null);  // Nếu không tìm thấy, trả về null
    }
}
