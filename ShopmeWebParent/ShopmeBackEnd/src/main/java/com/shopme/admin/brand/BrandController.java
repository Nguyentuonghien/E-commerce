package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

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
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/brands")
	public String listFirstPage() {
		return "redirect:/brands/page/1?sortField=name&sortDir=asc";
	}
	
	@GetMapping("/brands/page/{pageNumber}")
	public String listBrandsByPage(@PathVariable("pageNumber") int pageNumber, 
			      @PagingAndSortingParam(listName = "listBrands", moduleURL = "/brands") PagingAndSortingHelper helper, 
			      Model model) {
		brandService.listByPage(pageNumber, helper);
		return "brands/brands";
	}
	
	@GetMapping("/brands/newBrand")
	public String newBrand(Model model) {
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("brand", new Brand());
		model.addAttribute("pageTitle", "Create New Brand");
		return "brands/brand_form";
	}
	
	@PostMapping("/brands/saveBrand")
	public String saveBrand(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile, 
			    Model model, RedirectAttributes attributes) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);
			Brand savedBrand = brandService.save(brand);
			String brandDirectory = "../brand-logos/" + savedBrand.getId();
			FileUploadUtil.cleanDir(brandDirectory);
			FileUploadUtil.saveFile(brandDirectory, fileName, multipartFile);
		} else {
			brandService.save(brand);
		}
		attributes.addFlashAttribute("message", "The brand has been saved successfully.");
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/editBrand/{id}")
	public String editBrand(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
		try {
		   Brand brand = brandService.getById(id);
		   List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		   model.addAttribute("brand", brand);
		   model.addAttribute("listCategories", listCategories);
		   model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")");
		   return "brands/brand_form";
		} catch (BrandNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/brands";
		}   
	}
	
	@GetMapping("/brands/deleteBrand/{id}")
	public String deleteBrand(@PathVariable("id") Integer id,  Model model, RedirectAttributes attributes) {
		try {
			brandService.delete(id);
			String brandDirectory = "../brand-logos/" + id;
			FileUploadUtil.removeDir(brandDirectory);
			attributes.addFlashAttribute("message", "The brand ID " + id + " has been deleted successfully.");
		} catch (BrandNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/brands";
	}
	
}






