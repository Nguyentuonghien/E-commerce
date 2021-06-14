package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;

@Controller
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/categories")
	public String listFirstPage(@Param("sortDir") String sortDir, Model model) {
		return listByPage(1, model, sortDir, null);
	}
	
	@GetMapping("/categories/page/{pageNumber}")
	public String listByPage(@PathVariable("pageNumber") int pageNumber, Model model, 
			                 @Param("sortDir") String sortDir, 
			                 @Param("keyword") String keyword) {
		if(sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		List<Category> categories = categoryService.listByPage(pageInfo, pageNumber, sortDir, keyword);
		
		long startCount = (pageNumber - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
		if (endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
		
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("categories", categories);
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("moduleURL", "/categories");
		return "categories/categories";
	}

	@GetMapping("/categories/newCategory")
	public String newCategory(Model model) {
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		model.addAttribute("category", new Category());
		model.addAttribute("pageTitle", "Create New Category");
		model.addAttribute("listCategories", listCategories);
		return "categories/category_form";
	}

	@PostMapping("/categories/saveCategory")
	public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes attributes) throws IOException {
		if (!multipartFile.isEmpty()) {
			// lấy ra tên file từ đối tượng MultipartFile rồi gán cho category để lưu vào DB
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);
			Category savedCategory = categoryService.save(category);
			String uploadDir = "../category-images/" + savedCategory.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			categoryService.save(category);
		}
		attributes.addFlashAttribute("message", "The category has been saved successfully.");
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/editCategory/{id}")
	public String editCategory(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
		try {
			Category category = categoryService.getCategory(id);
			List<Category> categories = categoryService.listCategoriesUsedInForm();
			model.addAttribute("category", category);
			model.addAttribute("listCategories", categories);
			model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");
			return "categories/category_form";
		} catch (CategoryNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/categories";
		}
	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateCategoryEnabled(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, 
			                             RedirectAttributes attributes) {
		categoryService.updateCategoryEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The category ID " + id + " has been " + status;
		attributes.addFlashAttribute("message", message);
		return "redirect:/categories";
	} 
	
	@GetMapping("/categories/deleteCategory/{id}")
	public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes attributes) {
		try {
			// sau khi xóa category khỏi DB -> ta cũng sẽ xóa ảnh của nó
			categoryService.deleteCategory(id);
			String categoryDir = "../category-images/" + id;
			FileUploadUtil.removeDir(categoryDir);
			attributes.addFlashAttribute("message", "The category ID " + id + " has been deleted successfully.");
		} catch (CategoryNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/export/csv")
	public void exportCsv(HttpServletResponse response) throws IOException {
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		CategoryCsvExporter categoryCsvExporter = new CategoryCsvExporter();
		categoryCsvExporter.exportCsv(listCategories, response);
	}
	
}







