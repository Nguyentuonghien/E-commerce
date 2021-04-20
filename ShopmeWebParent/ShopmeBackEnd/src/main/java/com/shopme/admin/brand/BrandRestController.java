package com.shopme.admin.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandRestController {
	
	@Autowired
	private BrandService brandService;
	
	@PostMapping("/brands/check_unique")
	public String checkUniqueBrand(@Param("id") Integer id, @Param("name") String name) {
		return brandService.checkUnique(id, name);
	}
	
}
