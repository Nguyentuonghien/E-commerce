package com.shopme.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Brand;

@Service
public class BrandService {
	
	public static final int BRANDS_PER_PAGE = 10;
	
	@Autowired
	private BrandRepository brandRepository;
	
	public List<Brand> listAll() {
		return (List<Brand>) brandRepository.findAll();
	}
	
	public void listByPage(int pageNumber, PagingAndSortingHelper helper) {
		helper.listEntities(pageNumber, BRANDS_PER_PAGE, brandRepository);
	}
	
	public Brand save(Brand brand) {
		return brandRepository.save(brand);
	}
	
	public Brand getById(Integer id) throws BrandNotFoundException {
		try {
			return brandRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new BrandNotFoundException("Could not find any brand with ID " + id);
		}
	}
	
	public void delete(Integer id) throws BrandNotFoundException {
		Long countById = brandRepository.countById(id);
		if (countById == null || countById == 0) {
			throw new BrandNotFoundException("Could not find brand with ID " + id);
		}
		brandRepository.deleteById(id);
	}
	
	public String checkUnique(Integer id, String name) {
		Brand brandByName = brandRepository.findByName(name);
		if (id == null || id == 0) {
			if (brandByName != null) {
				return "Duplicate Name";
			}
		} else {
			if (brandByName != null && brandByName.getId() != id) {
				return "Duplicate Name";
			}
		}
		return "OK";
	}
	
	
}





