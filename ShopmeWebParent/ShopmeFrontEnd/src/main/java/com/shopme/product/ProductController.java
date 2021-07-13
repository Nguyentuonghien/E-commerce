package com.shopme.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.CategoryNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;

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
		try {
			Category category = categoryService.findByAliasEnabled(alias);
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
			return "product/products_by_category";
		} catch (CategoryNotFoundException ex) {
			return "error/404";
		}	
	}

	@GetMapping("/p/{product_alias}")
	public String viewProductDetail(@PathVariable("product_alias") String alias, Model model) {
		try {
			Product product = productService.getByAlias(alias);
			List<Category> listParentsCategory = categoryService.getParentsCategory(product.getCategory());
			model.addAttribute("product", product);
			model.addAttribute("listParentsCategory", listParentsCategory);
			model.addAttribute("pageTitle", product.getShortName());
			return "product/product_detail";
		} catch (ProductNotFoundException e) {
			return "error/404";
		}
	}
	
	@GetMapping("/search")
	public String searchFirstPage(@RequestParam("keyword") String keyword, Model model) {
		return searchProductByPage(keyword, model, 1);
	}
	
	@GetMapping("/search/page/{pageNumber}")
	public String searchProductByPage(@RequestParam("keyword") String keyword, Model model, 
			       @PathVariable("pageNumber") int pageNumber) {
		Page<Product> pageProducts = productService.search(pageNumber, keyword);
		List<Product> listResult = pageProducts.getContent();
		long startCount = (pageNumber - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;
		if (endCount > pageProducts.getTotalElements()) {
			endCount = pageProducts.getTotalElements();
		}
		
		model.addAttribute("listResult", listResult);
		model.addAttribute("keyword", keyword);
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("pageTitle", keyword + " - Search Result");
		return "product/search_result";
	}
	
}






