package com.shopme.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;

@Service
public class ProductService {
	
	public static final int PRODUCTS_PER_PAGE = 10;
	
	@Autowired
	private ProductRepository productRepository;
	
	public Page<Product> listByCategory(int pageNumber, Integer categoryId) {
		Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCTS_PER_PAGE);
		String categoryIDMatch = "-" + String.valueOf(categoryId) + "-";
		Page<Product> listProducts = productRepository.listProductsByCategory(categoryId, categoryIDMatch, pageable);
		return listProducts;
	}
	
	
	
}
