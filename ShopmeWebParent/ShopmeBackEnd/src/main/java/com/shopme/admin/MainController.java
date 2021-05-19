package com.shopme.admin;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	/**
	 * SecurityContext là interface cốt lõi của Spring Security, lưu trữ tất cả các chi tiết liên quan đến bảo mật trong ứng dụng. 
	 * Khi chúng ta kích hoạt Spring Security thì SecurityContext cũng sẽ được kích hoạt theo.
	 * Chúng ta sẽ không truy cập trực tiếp vào SecurityContext, thay vào đó sẽ sử dụng lớp SecurityContextHolder. 
	 * Lớp này lưu trữ security context hiện tại của ứng dụng, bao gồm chi tiết của principal đang tương tác với ứng dụng. 
	 * Spring Security sẽ dùng một đối tượng Authentication để biểu diễn thông tin này
	 */
	@GetMapping("/login")
	public String loginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// kiểm tra xem có người dùng đã xác thực hay không ?
		// Spring Security theo mặc định sẽ đặt AnonymousAuthenticationToken làm authentication trên SecurityContextHolder nếu như ta chưa login
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
		    return "login";
		}   
		return "redirect:/";
	}
	
}




