package com.shopme.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;

@RestController
public class ProductRestController {
	
	@Autowired
	private ProductService productService;
	
	@PostMapping("/products/check_unique")
	public String checkProductUnique(@RequestParam("id") Integer id, @RequestParam("name") String name) {
		return productService.checkUnique(name, id);
	}
	
	@GetMapping("/products/get/{id}")
	public ProductDTO getProductInfo(@PathVariable("id") Integer id) throws ProductNotFoundException {
		Product product = productService.getProduct(id);
		return new ProductDTO(product.getName(), product.getMainImagePath(), 
				              product.getDiscountPrice(), product.getCost());
	}
	
}
