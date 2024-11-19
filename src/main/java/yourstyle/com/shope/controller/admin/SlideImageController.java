package yourstyle.com.shope.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import yourstyle.com.shope.model.Slide;
import yourstyle.com.shope.repository.SlideRepository;
import yourstyle.com.shope.service.SlideService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	        
	        try {
	            // Kiểm tra thư mục upload
	            File uploadDir = new File(UPLOAD_DIR);
	            if (!uploadDir.exists()) {
	                uploadDir.mkdirs();
	            }
	            
	            // Lưu ảnh chính
	            String mainImagePath = saveImage(mainImage);

	            // Lưu ảnh phụ
	            for (MultipartFile additionalImage : additionalImages) {
	                saveImage(additionalImage); // Lưu từng ảnh phụ
	            }

	            response.put("success", true);
	            response.put("mainImage", mainImagePath);
	            response.put("message", "Images uploaded successfully");
	        } catch (IOException e) {
	            response.put("success", false);
	            response.put("message", "Error: " + e.getMessage());
	        }
	        
	        return ResponseEntity.ok(response);
	    }

	    // Phương thức lưu ảnh vào thư mục upload
	    private String saveImage(MultipartFile image) throws IOException {
	        String fileName = image.getOriginalFilename();
	        Path path = Paths.get(UPLOAD_DIR, fileName);
	        Files.write(path, image.getBytes());
	        return path.toString(); // Trả về đường dẫn đến ảnh đã lưu
	    }
}