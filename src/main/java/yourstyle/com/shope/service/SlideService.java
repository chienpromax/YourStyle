package yourstyle.com.shope.service;

import yourstyle.com.shope.model.Slide;
import java.util.List;

public interface SlideService {

    // Thêm một slide mới
    void addSlide(Slide slide);

    // Chỉnh sửa một slide
    void editSlide(Slide slide);

    // Xóa slide theo ID
    void deleteSlide(int slideId);

    // Lấy danh sách tất cả các slide
    List<Slide> getAllSlides();

    // Lấy slide theo ID
    Slide getSlideById(int slideId);
}