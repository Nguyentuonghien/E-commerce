package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Service
@Transactional
public class ProductService {
	
	public static final int PRODUCTS_PER_PAGE = 5;
	
	@Autowired
	private ProductRepository productRepository;
	
	public List<Product> findAll() {
		return (List<Product>) productRepository.findAll();
	}
	
	public Page<Product> listProductsByPage(String keyword, int pageNumber, String sortField, String sortDir, Integer categoryId) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCTS_PER_PAGE, sort);
		
		// có search theo keyword và có select products theo category
		if (keyword != null && !keyword.isEmpty()) {
			if (categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				return productRepository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
			}
			return productRepository.findAllProducts(keyword, pageable); // chỉ search 
		}
		// không search theo keyword mà chỉ select products theo category
		if (categoryId != null && categoryId > 0) {
			String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
			return productRepository.findAllInCategory(categoryId, categoryIdMatch, pageable);
		}
		return productRepository.findAll(pageable);
 	}
	
	public Product saveProduct(Product product) {
		if (product.getId() == null) {
			product.setCreatedTime(new Date());
		}
		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replaceAll(" ", "-");
			product.setAlias(defaultAlias);
		} else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}
		product.setUpdatedTime(new Date());
		return productRepository.save(product); 
	}
	
	// salesperson chỉ được thay đổi price,cost,discount của product khi edit -> salesperson chỉ save product với 3 field đó khi edit
	public void saveProductPrice(Product productInForm) {
		Product productInDB = productRepository.findById(productInForm.getId()).get();
		productInDB.setPrice(productInForm.getPrice());
		productInDB.setCost(productInForm.getCost());
		productInDB.setDiscountPercent(productInForm.getDiscountPercent());
		productRepository.save(productInDB);
	}
	
	public String checkUnique(String name, Integer id) {
		Product productByName = productRepository.findByName(name);
		if (id == null || id == 0) {
			if (productByName != null) {
				return "Duplicate Name";
			}
		} else {
			if (productByName != null && productByName.getId() != id) {
				return "Duplicate Name";
			}
		}
		return "OK";
	}
	
	public void updateProductEnabledStatus(Integer id, boolean status) {
		productRepository.updateEnabledStatus(id, status);
	}
	
	public void deleteProduct(Integer id) throws ProductNotFoundException {
		Long countById = productRepository.countById(id);
		if (countById == null || countById == 0) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}
		productRepository.deleteById(id);
	}
	
	public Product getProduct(Integer id) throws ProductNotFoundException {
		try {
			return productRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ProductNotFoundException("Could not found product with ID: " + id);
		}
	}
	
}




