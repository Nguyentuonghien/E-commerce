package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/brands")
	public String listFirstPage(Model model) {
		return listBrandsByPage(1, model, "name", "asc", null);
	}
	
	@GetMapping("/brands/page/{pageNumber}")
	public String listBrandsByPage(@PathVariable("pageNumber") int pageNumber, Model model, 
			         @Param("sortField") String sortField, 
			         @Param("sortDir") String sortDir,
			         @Param("keyword") String keyword) {
		Page<Brand> page = brandService.listByPage(pageNumber, keyword, sortDir);
		List<Brand> listBrands = page.getContent();
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		long startCount = (pageNumber - 1) * BrandService.BRANDS_PER_PAGE + 1;
		long endCount = startCount + BrandService.BRANDS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
	    model.addAttribute("keyword", keyword);
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






