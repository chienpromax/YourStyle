package yourstyle.com.shope.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import yourstyle.com.shope.model.Slide;
import yourstyle.com.shope.repository.SlideRepository;
import yourstyle.com.shope.service.SlideService;
import yourstyle.com.shope.utils.UploadUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/slides")
public class SlideImageController {

    private static final String UPLOAD_DIR = "src\\main\\resources\\static\\uploads\\slide";

    @Autowired
    private SlideRepository slideRepository;
    @Autowired
    private SlideService slideService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImages(@RequestParam("mainImage") MultipartFile mainImage,
            @RequestParam("additionalImages") MultipartFile[] additionalImages) {
        Map<String, Object> response = new HashMap<>();

        slideRepository.deleteAll(); // Xóa tất cả ảnh slide trước đó
        try {
            // Xử lý ảnh chính
            String mainImageName = null;
            if (mainImage != null && !mainImage.isEmpty()) {
                mainImageName = UUID.randomUUID().toString() + "_" + mainImage.getOriginalFilename();
                String savedMainImage = UploadUtils.saveFile(UPLOAD_DIR, mainImageName, mainImage);
                slideRepository.save(new Slide(savedMainImage)); // Lưu thông tin ảnh vào DB
            }

            // Xử lý ảnh bổ sung
            for (MultipartFile file : additionalImages) {
                if (!file.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    String savedFileName = UploadUtils.saveFile(UPLOAD_DIR, fileName, file);
                    slideRepository.save(new Slide(savedFileName)); // Lưu thông tin ảnh vào DB
                }
            }

            // Trả về danh sách ảnh sau khi upload
            List<Slide> slides = slideService.getAllSlides();
            response.put("success", true);
            response.put("message", "Ảnh đã được lưu thành công!");
            response.put("redirectUrl", "/yourstyle/home");
            response.put("slides", slides.toString());
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lưu ảnh: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}
