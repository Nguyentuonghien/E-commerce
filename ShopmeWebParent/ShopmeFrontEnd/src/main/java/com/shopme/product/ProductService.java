package com.shopme.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Service
public class ProductService {
	
	public static final int PRODUCTS_PER_PAGE = 10;
	public static final int SEARCH_RESULTS_PER_PAGE = 10;
	
	@Autowired
	private ProductRepository productRepository;
	
	public Page<Product> listByCategory(int pageNumber, Integer categoryId) {
		Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCTS_PER_PAGE);
		String categoryIDMatch = "-" + String.valueOf(categoryId) + "-";
		Page<Product> listProducts = productRepository.listProductsByCategory(categoryId, categoryIDMatch, pageable);
		return listProducts;
	}
	
	public Product getByAlias(String alias) throws ProductNotFoundException {
		Product product = productRepository.findByAlias(alias);
		if (product == null) {
			throw new ProductNotFoundException("Could not find any product with alias: " + alias);
		}
		return product;
	}
	
	public Page<Product> search(int pageNumber, String keyword) {
		Pageable pageable = PageRequest.of(pageNumber - 1, SEARCH_RESULTS_PER_PAGE);
		return productRepository.search(keyword, pageable);
	}
	
}



