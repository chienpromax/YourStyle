package yourstyle.com.shope.controller.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import yourstyle.com.shope.service.ProductImageService;

@RestController
@RequestMapping("/api/products")
public class ProductImageController {

    @Autowired
    private ProductImageService productImageService;

    @PostMapping("/{productId}/images")
    public ResponseEntity<Void> uploadProductImages(
            @PathVariable Integer productId,
            @RequestParam("imageFiles") MultipartFile[] imageFiles) {
        productImageService.saveProductImages(productId, imageFiles);
        return ResponseEntity.ok().build();
    }
    
}
