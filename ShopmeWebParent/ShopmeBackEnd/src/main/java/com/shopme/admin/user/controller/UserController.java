package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/users/newUser")
	public String newUser(Model model) {
		List<Role> listRoles = userService.getListRole();
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");
		return "users/user_form";
	}
	
	@GetMapping("/users")
	public String listFirstPage() {
		return "redirect:/users/page/1?sortField=firstName&sortDir=asc";
	}
	
	@GetMapping("/users/page/{pageNum}")
	public String listUserByPage(@PathVariable("pageNum") int pageNumber, 
			      @PagingAndSortingParam(listName = "listUsers", moduleURL = "/users") PagingAndSortingHelper helper,
			      Model model) {
		userService.listByPage(pageNumber, helper);
		return "users/users";
	}
	
	@PostMapping("/users/saveUser")
	public String saveUser(User user, @RequestParam("image") MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException {
		// nếu form đã có file được upload lên(không rỗng), ta lấy ra tên của file uploaded(tệp được tải lên) đó và gán cho trường photos của User và lưu vào trong DB
		// vì ta chỉ lưu trữ tên file trong DB(vd: tuongnh.png) và file uploaded thực tế được lưu trữ trong hệ thống tệp(file system)
 		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = userService.save(user);
			// trong thư mục user-photos sẽ chứa 1 thư mục con = id của user mà ta upload ảnh, thư mục con đó sẽ chứa ảnh ta uploaded lên
			String uploadDir = "user-photos/" + savedUser.getId();			
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			// nếu form chưa có file được uploaded lên, nếu user k đặt ảnh đại diện -> set photos rỗng
			if(user.getPhotos().isEmpty()) {
				user.setPhotos(null);
			}
			userService.save(user);
		}
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully!");
		
		return getRedirectURLToAffectedUser(user);
	}

	private String getRedirectURLToAffectedUser(User user) {
		String firstPastOfEmail = user.getEmail().split("@")[0];		
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPastOfEmail;
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
		try {
			User user = userService.getUserById(id);
			List<Role> listRoles = userService.getListRole();
			model.addAttribute("user", user);
			model.addAttribute("listRoles", listRoles);
			model.addAttribute("pageTitle", "Edit User (ID: " + id +")");
			return "users/user_form";
		} catch (UserNotFoundException ex) {
			// khong tim thay user trong DB --> bao loi
			attributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/users";
		}
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes attributes) {
		try {
			userService.deleteUser(id);
			attributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully!");
		} catch (UserNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
		}		
		return "redirect:/users";
	}
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes attributes) {
		userService.updateUserEnabledStatus(id, enabled);
		// status="enabled" neu enabled=true, con khong status="disabled"
		String status = enabled ? "enabled" : "disabled";
		String message = "The user ID " + id + " has been " + status;
		attributes.addFlashAttribute("message", message);
		return "redirect:/users";
	}
	
	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> listUsers = userService.listAll();
		UserCsvExporter userCsvExporter = new UserCsvExporter();
		userCsvExporter.exportCSV(listUsers, response);
	}
	
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> users = userService.listAll();
		UserExcelExporter excelExporter = new UserExcelExporter();
		excelExporter.exportExcel(users, response);
	}
	
	@GetMapping("/users/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws IOException {
		List<User> users = userService.listAll();
		UserPdfExporter userPdfExporter = new UserPdfExporter();
		userPdfExporter.exportToPdf(users, response);
	}
	
}







