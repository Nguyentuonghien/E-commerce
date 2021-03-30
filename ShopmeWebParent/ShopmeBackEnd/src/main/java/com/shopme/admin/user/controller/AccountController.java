package com.shopme.admin.user.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.User;


@Controller
public class AccountController {
	
	@Autowired
	private UserService userService;
	
	/**
	 * @AuthenticationPrincipal: khi ta muốn lấy đối tượng UserDetails đại diện cho user đang đăng nhập
       @AuthenticationPrincipal sẽ inject một principal nếu user được authenticate hoặc null nếu không có user nào được authenticate                     
	 */	
	
	@GetMapping("/account")
	public String viewDetails(@AuthenticationPrincipal ShopmeUserDetails loggedUser, Model model) {
		// khi user login thanh cong -> UserDetails se chua cac thong tin cua user do(email, password, roles), ta se su dung 1 instance cua UserDetail
		// la ShopmeUserDetails de lay ra email cua user do va tu do se tim trong DB theo email de lay ra 1 doi tuong user hoan chinh
		String email = loggedUser.getUsername();
		// lấy ra 1 đối tượng user từ DB theo email 
		User user = userService.getByEmail(email);
		model.addAttribute("user", user);
		return "users/account_form";
	}
	
	@PostMapping("/account/update")
	public String saveDetails(User user, @RequestParam("image") MultipartFile multipartFile, 
			                  @AuthenticationPrincipal ShopmeUserDetails loggedUser, RedirectAttributes attributes) throws IOException{
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = userService.updateAccount(user);
			String uploadDir = "user-photos/" + savedUser.getId(); 
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if(user.getPhotos().isEmpty()) {
				user.setPhotos(null);
			}
			userService.updateAccount(user);
		}
		// show firstName, lastName được update bởi user lên navigation và hiển thị message update thành công
		loggedUser.setFirstName(user.getFirstName());
		loggedUser.setLastName(user.getLastName());
		attributes.addFlashAttribute("message", "Your account details have been updated.");
		return "redirect:/account";
	}
	
}




