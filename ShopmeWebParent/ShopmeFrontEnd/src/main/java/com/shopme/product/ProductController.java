package com.shopme.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/c/{category_alias}")
	public String viewFirstPage(Model model, @PathVariable("category_alias") String alias) {
		return viewCategoryByPage(model, alias, 1);
	}

	@GetMapping("/c/{category_alias}/page/{pageNumber}")
	public String viewCategoryByPage(Model model, @PathVariable("category_alias") String alias,
			      @PathVariable("pageNumber") int pageNumber) {
		Category category = categoryService.findByAliasEnabled(alias);
		if (category == null) {
			return "error/404";
		}
		List<Category> listParentsCategory = categoryService.getParentsCategory(category);
		Page<Product> pageProducts = productService.listByCategory(pageNumber, category.getId());
		List<Product> listProducts = pageProducts.getContent();
		long startCount = (pageNumber - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > pageProducts.getTotalElements()) {
			endCount = pageProducts.getTotalElements();
		}

		model.addAttribute("category", category);
		model.addAttribute("listParentsCategory", listParentsCategory);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("pageTitle", category.getName());
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("totalPages", pageProducts.getTotalPages());
		return "products_by_category";
	}

}
