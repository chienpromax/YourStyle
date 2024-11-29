package yourstyle.com.shope.controller.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import yourstyle.com.shope.service.AccountService;  
@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private AccountService accountService;

    @ModelAttribute
    public void addAvatarToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String userName = authentication.getName();
            String avatarUrl = accountService.getAvatarByUsername(userName); 
            model.addAttribute("avatar", avatarUrl); 
        }
    }

}
