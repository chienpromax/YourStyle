package yourstyle.com.shope.controller.admin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import yourstyle.com.shope.model.Category;
import yourstyle.com.shope.service.CategoryService;
import yourstyle.com.shope.utils.UploadUtils;

@Controller
@RequestMapping("admin/categories/")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("add")
    public String add(@RequestParam("name") String categoryName,
            @RequestParam(value = "parentCategory", required = false) String parentId,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {
        try {
            Category category = new Category();
            category.setName(categoryName);

            // Thiết lập danh mục cha nếu có
            if (parentId != null && !parentId.isEmpty()) {
                Category parentCategory = categoryService.findById(Integer.parseInt(parentId)).orElse(null);
                category.setParentCategory(parentCategory);
            }

            // Xử lý file upload
            if (!imageFile.isEmpty()) {
                String uploadDir = "src/main/resources/static/uploads"; // Đường dẫn lưu file trong static
                Path uploadPath = Paths.get(uploadDir);

                // Tạo thư mục nếu chưa tồn tại
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Tạo tên file duy nhất
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);

                // Sao chép file tới thư mục lưu trữ
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Thiết lập đường dẫn hình ảnh cho category
                category.setImage(fileName); // Lưu tên file (để hiển thị đúng)
            }

            // Lưu danh mục mới
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("message", "Thêm danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Thêm danh mục thất bại:Tên danh mục đã tồn tại ");
            e.printStackTrace();
        }
        return "redirect:/admin/categories/list";
    }

   

    @PostMapping("/update")
public ModelAndView updateCategory(
        @ModelAttribute("category") Category category,
        BindingResult result,
        @RequestParam("imageFile") MultipartFile imageFile,
        ModelMap model,
        RedirectAttributes redirectAttributes) {

    try {
        // Xử lý validate nếu có lỗi
        if (result.hasErrors()) {
            return new ModelAndView("admin/categories/addOrEdit", model);
        }

        // Kiểm tra và xử lý danh mục cha nếu có
        if (category.getParentCategory() != null && category.getParentCategory().getCategoryId() != null) {
            Category parentCategory = categoryService.findById(category.getParentCategory().getCategoryId())
                    .orElse(null);
            category.setParentCategory(parentCategory);
        }

        // Kiểm tra và xử lý file ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            String contentType = imageFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                result.rejectValue("image", "error.category", "File không hợp lệ, vui lòng chọn một file ảnh.");
                return new ModelAndView("admin/categories/addOrEdit", model);
            }

            // Upload tệp ảnh
            try {
                String uploadDir = "src/main/resources/static/uploads/";
                String originalFilename = imageFile.getOriginalFilename();
                String newFilename = UploadUtils.saveFile(uploadDir, originalFilename, imageFile);
                category.setImage(newFilename);
            } catch (IOException e) {
                model.addAttribute("messageType", "error");
                model.addAttribute("messageContent", "Lỗi khi tải tệp: " + e.getMessage());
                return new ModelAndView("admin/categories/addOrEdit", model);
            }
        } else {
            // Nếu không có tệp ảnh mới và danh mục chưa có ảnh thì báo lỗi
            if (category.getCategoryId() != null) {
                Optional<Category> existingCategory = categoryService.findById(category.getCategoryId());
                if (existingCategory.isPresent()) {
                    category.setImage(existingCategory.get().getImage());
                } else if (category.getImage() == null || category.getImage().isEmpty()) {
                    result.rejectValue("image", "error.category", "Vui lòng chọn một ảnh cho danh mục.");
                    return new ModelAndView("admin/categories/addOrEdit", model);
                }
            }
        }

        // Lưu danh mục
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("message", "Cập nhật danh mục thành công!");

    } catch (Exception e) {
        result.rejectValue("name", "error.category", "Cập nhật danh mục thất bại: Tên danh mục đã tồn tại");
        e.printStackTrace();
        return new ModelAndView("admin/categories/addOrEdit", model);
    }

    return new ModelAndView("redirect:/admin/categories/list");
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
            System.out.println(id);
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xóa danh mục thất bại!");
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
    public ResponseEntity<?> toggleCategoryStatus(@PathVariable("categoryId") Integer categoryId,
            @RequestBody Map<String, Boolean> statusData) {
        try {
            boolean newStatus = statusData.get("status");
            Category category = categoryService.findById(categoryId).get();
            if (category != null) {
                category.setStatus(newStatus);
                categoryService.save(category);
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
