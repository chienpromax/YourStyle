package yourstyle.com.shope.controller.admin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.service.CategoryService;

@Controller
@RequestMapping("admin/categories/")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("add")
public String add(@RequestParam("name") String categoryName,
                  @RequestParam(value = "parentCategory", required = false) String parentId,
                  @RequestParam("image") MultipartFile imageFile,
                  RedirectAttributes redirectAttributes) {
    try {
        Category category = new Category();
        category.setName(categoryName);

        // Kiểm tra và thiết lập danh mục cha nếu có
        if (parentId != null && !parentId.isEmpty()) {
            Category parentCategory = categoryService.findById(Integer.parseInt(parentId)).orElse(null);
            category.setParentCategory(parentCategory);
            System.out.println(parentId + "------------------------------" + (parentCategory != null ? parentCategory.getName() : "null"));
        }

        // Xử lý file upload
        if (!imageFile.isEmpty()) {
            // Đường dẫn lưu file - bạn có thể thay đổi đường dẫn này theo ý muốn
            String uploadDir = "src/main/resources/static/images/";
            Path uploadPath = Paths.get(uploadDir);
            
            // Tạo thư mục nếu chưa tồn tại
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file duy nhất để tránh ghi đè
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            
            // Sao chép file tới thư mục lưu trữ
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Thiết lập đường dẫn hình ảnh cho category
            category.setImage("/images/" + fileName);
        }

        // Lưu danh mục mới
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("message", "Thêm danh mục thành công!");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Thêm danh mục thất bại: " + e.getMessage());
        e.printStackTrace();
    }
    return "redirect:/admin/categories/list"; // Điều hướng về danh sách danh mục
}

    @GetMapping("/list")
    public String listCategories(Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryService.findAll(pageable);

        model.addAttribute("categoryPage", categoryPage);

        /////////////////
        List<Category> listcategoryparents = categoryService.findByParentCategoryIsNull();
        model.addAttribute("listctparents", listcategoryparents);

        return "admin/categories/addOrEdit";
    }

    // Xóa danh mục
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xóa danh mục thất bại!");
        }
        return "redirect:/admin/categories/list";
    }

    @PostMapping("/update")
    public String updateCategory(@RequestParam("categoryId") Integer categoryId,
                                 @RequestParam("name") String categoryName,
                                 @RequestParam(value = "parentCategory", required = false) String parentId,
                                 @RequestParam("image") MultipartFile imageFile,
                                 RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Invalid category ID:" + categoryId));
    
            category.setName(categoryName);
    
            // Cập nhật danh mục cha nếu có
            if (parentId != null && !parentId.isEmpty()) {
                Category parentCategory = categoryService.findById(Integer.parseInt(parentId)).orElse(null);
                category.setParentCategory(parentCategory);
            }
    
            // Xử lý file upload nếu có
            if (!imageFile.isEmpty()) {
                String uploadDir = "src/main/resources/static/images/";
                Path uploadPath = Paths.get(uploadDir);
    
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
    
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
    
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                category.setImage("/images/" + fileName);
            }
    
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("message", "Cập nhật danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cập nhật danh mục thất bại: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/categories/list";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable("id") Integer id, Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size) {
        try {
            // Lấy danh mục cần sửa
            Category category = categoryService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category ID:" + id));
    
            // Lấy danh sách các danh mục cha
            List<Category> listcategoryparents = categoryService.findByParentCategoryIsNull();
    
            // Lấy danh sách danh mục và phân trang
            Pageable pageable = PageRequest.of(page, size);
            Page<Category> categoryPage = categoryService.findAll(pageable);
    
            // Đổ dữ liệu danh mục lên form
            model.addAttribute("category", category);
            model.addAttribute("listctparents", listcategoryparents);
    
            // Đổ dữ liệu danh sách danh mục vào bảng
            model.addAttribute("categoryPage", categoryPage);
    
            return "admin/categories/addOrEdit"; // Hiển thị form và danh sách
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/categories/list";
        }
    }
    



    @PostMapping("toggleStatus/{categoryId}")
    @ResponseBody
    public ResponseEntity<?> toggleAccountStatus(@PathVariable("categoryId") Integer categoryId,
            @RequestBody Map<String, Boolean> statusData) {
        try {
            boolean newStatus = statusData.get("status");
            Category account = categoryService.findById(categoryId).get();
            if (account != null) {
                account.setStatus(newStatus);
                categoryService.save(account);
                return ResponseEntity.ok(Collections.singletonMap("success", true));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("success", false));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("success", false));
        }
    }
}
