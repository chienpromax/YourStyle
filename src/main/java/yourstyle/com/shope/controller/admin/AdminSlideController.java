package yourstyle.com.shope.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import yourstyle.com.shope.model.Slide;
import yourstyle.com.shope.repository.SlideRepository;
import yourstyle.com.shope.service.SlideService;

@Controller
@RequestMapping("/admin/slide")
public class AdminSlideController {

	@Autowired
	private SlideService slideService;
	@Autowired
	private SlideRepository slideRepository;

	// Hiển thị form thêm Slide mới
	@GetMapping("/add")
	public String addSlideForm(Model model) {

		List<Slide> slides = slideService.getAllSlides();

		for (Slide slide : slides) {
			String[] imagePathsArray = slide.getImagePaths().split(",");
			slide.setImagePathsArray(imagePathsArray);
		}

		model.addAttribute("slides", slides);

		return "admin/slides/addSlide";
	}

	// @GetMapping("/slides")
	// public ModelAndView showSlides() {
	// ModelAndView modelAndView = new ModelAndView("slideView"); // Tên view của
	// bạn

	// // Lấy dữ liệu slide từ cơ sở dữ liệu
	// List<Slide> slides = slideRepository.findAll();

	// // Truyền dữ liệu slide vào model
	// modelAndView.addObject("slides", slides);

	// return modelAndView;
	// }

}
