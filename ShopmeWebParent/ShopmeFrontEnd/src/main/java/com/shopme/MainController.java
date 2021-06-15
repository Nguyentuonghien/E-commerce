package com.shopme;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;

@Controller
public class MainController {
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/")
	public String viewHomePage(Model model) {
		List<Category> listCategories = categoryService.listNoChildrenCategories();
		model.addAttribute("listCategories", listCategories);
		return "index";
	}
	
	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// kiểm tra xem có người dùng đã xác thực hay chưa ? nếu chưa sẽ chuyển về trang login
		// Spring Security theo mặc định sẽ đặt AnonymousAuthenticationToken làm authentication trên SecurityContextHolder nếu như ta chưa login
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
		    return "login";
		}   
		return "redirect:/";
	}
	
}
