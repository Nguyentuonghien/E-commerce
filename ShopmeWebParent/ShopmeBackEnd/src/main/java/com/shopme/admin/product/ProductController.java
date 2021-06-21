package com.shopme.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {
	
	@Autowired
	private ProductService productService;

	@Autowired
	private BrandService brandService;

	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return listByPage(model, 1, "name", "asc", null, 0);
	}
	
	@GetMapping("/products/page/{pageNumber}")
	public String listByPage(Model model, @PathVariable("pageNumber") int pageNumber, 
			        @Param("sortField") String sortField, 
			        @Param("sortDir") String sortDir, 
			        @Param("keyword") String keyword,
	                @Param("categoryId") Integer categoryId) {
		Page<Product> page = productService.listProductsByPage(keyword, pageNumber, sortField, sortDir, categoryId);
		List<Product> products = page.getContent();		
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		long startCount = (pageNumber - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";					
		if (categoryId != null) {
			model.addAttribute("categoryId", categoryId);
		}
		
		model.addAttribute("products", products);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("moduleURL", "/products");
		return "products/products";
	}

	@GetMapping("/products/newProduct")
	public String newProduct(Model model) {
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		List<Brand> listBrands = brandService.listAll();
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("product", product);
		model.addAttribute("pageTitle", "Create New Product");
		model.addAttribute("numberOfExistingExtraImages", 0);
		return "products/product_form";
	}
	
	/**
	 * hàm saveProduct phải có parameter để đọc các giá trị từ input bên html(fileImage,detailIDs,imageIDs,...) 
	 *  -> @RequestParam(): mapping với hidden field với name="detailIDs",... bên html
	 * vì có thể có nhiều ảnh phụ, detail,... nên ta dùng mảng  MultipartFile[], String[],...
	 * 
	 * @AuthenticationPrincipal: sẽ inject một principal nếu user được xác thực(authenticate) hoặc null nếu không có user nào được authenticate.
	 */
	@PostMapping("/products/saveProduct")
	public String saveProduct(Product product, RedirectAttributes attributes,
			     @RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,
			     @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImagesMultipart,
			     @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			     @RequestParam(name = "detailNames", required = false) String[] detailNames,
			     @RequestParam(name = "detailValues", required = false) String[] detailValues,
			     @RequestParam(name = "imageIDs", required = false) String[] imageIDs, 
			     @RequestParam(name = "imageNames", required = false) String[] imageNames, 
			     @AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException {	
		
		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
			if (loggedUser.hasRole("Salesperson")) {
				productService.saveProductPrice(product);
				attributes.addFlashAttribute("message", "The product has been saved successfully.");
				return "redirect:/products";
			}
		}
		
		ProductSaveHelper.setMainImageName(mainImageMultipart, product);
		ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
		ProductSaveHelper.setNewExtraImageNames(extraImagesMultipart, product);
		ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);

		Product savedProduct = productService.saveProduct(product);
		
		ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImagesMultipart, savedProduct);
		ProductSaveHelper.deleteExtraImagesWereRemovedOnForm(product);
		
		attributes.addFlashAttribute("message", "The product has been saved successfully.");
		return "redirect:/products";
	}

	@GetMapping("/products/editProduct/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes, 
			                  @AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		try {
			Product product = productService.getProduct(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistingExtraImages = product.getProductImages().size();
			Integer numberOfExistingDetail = product.getProductDetails().size();
			
			boolean isReadOnlyForSalesperson = false;
			if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
				if (loggedUser.hasRole("Salesperson")) {
					isReadOnlyForSalesperson = true;
				}
			}
			model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);
			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit Product (ID:" + id + ")");
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
			model.addAttribute("numberOfExistingDetail", numberOfExistingDetail);
			return "products/product_form";
		} catch (ProductNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/products";
		}
	}
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes attributes) {
		productService.updateProductEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The Product ID " + id + " has been " + status;
		attributes.addFlashAttribute("message", message);
		return "redirect:/products";
	}

	@GetMapping("/products/deleteProduct/{id}")
	public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes attributes) {
		try {
			productService.deleteProduct(id);
			// xóa extra image trước rồi mới xóa main image
			String productExtraImagesDir = "../product-images/" + id + "/extras";
			String productMainImagesDir = "../product-images/" + id;
			FileUploadUtil.removeDir(productExtraImagesDir);
			FileUploadUtil.removeDir(productMainImagesDir);
			attributes.addFlashAttribute("message", "The product ID " + id + " has been deleted successfully.");
		} catch (ProductNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/products";
	}
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
		try {
			Product product = productService.getProduct(id);
			model.addAttribute("product", product);
			return "products/product_detail_modal";
		} catch (ProductNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/products";
		}
	}
	
	
}





