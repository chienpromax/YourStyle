package yourstyle.com.shope.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import yourstyle.com.shope.model.Slide;
import yourstyle.com.shope.repository.SlideRepository;
import yourstyle.com.shope.service.SlideService;

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

		slideRepository.deleteAll();
		try {
			Path uploadDir = Paths.get(UPLOAD_DIR);
			Files.createDirectories(uploadDir);

			String mainImageName = null;
			if (mainImage != null && !mainImage.isEmpty()) {
				mainImageName = UUID.randomUUID().toString() + "_" + mainImage.getOriginalFilename();
				Path mainImagePath = uploadDir.resolve(mainImageName);
				mainImage.transferTo(mainImagePath);
				slideRepository.save(new Slide(mainImageName));
			}

			for (MultipartFile file : additionalImages) {
				if (!file.isEmpty()) {
					String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
					Path filePath = uploadDir.resolve(fileName);
					file.transferTo(filePath);
					slideRepository.save(new Slide(fileName));
				}
			}
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