package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {
	
	@Autowired
	private BrandRepository brandRepository;
	
	@Test
	public void testCreateBrand1() {
		Category category = new Category(6);
		Brand brand = new Brand("Acer");
		brand.getCategories().add(category);
		Brand saveBrand = brandRepository.save(brand);
		
		assertThat(saveBrand).isNotNull();
		assertThat(saveBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateBrand2() {
		Category cellPhone = new Category(4);
		Category tablet = new Category(7);
		Brand brand = new Brand("Apple");
		brand.getCategories().addAll( Arrays.asList(cellPhone, tablet));
		
		Brand saveBrand = brandRepository.save(brand);
		
		assertThat(saveBrand).isNotNull();
		assertThat(saveBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateBrand3() {
		Category memory = new Category(29);
		Category ihd = new Category(24);
		Brand brand = new Brand("Samsung");
		brand.getCategories().addAll( Arrays.asList(memory, ihd));
		
		Brand saveBrand = brandRepository.save(brand);
		
		assertThat(saveBrand).isNotNull();
		assertThat(saveBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllBrands() {
		Iterable<Brand> brands = brandRepository.findAll();
		brands.forEach(System.out::println);
		
		assertThat(brands).isNotEmpty();
	}
	
	@Test
	public void testGetById() {
		Brand brand = brandRepository.findById(1).get();
		
		assertThat(brand.getName()).isEqualTo("Acer");
	}
	
	@Test
	public void testUpdateBrand() {
		Brand brand = brandRepository.findById(3).get();
		brand.setName("Samsung electronics");
		Brand saveBrand = brandRepository.save(brand);
		
		assertThat(saveBrand.getName()).isEqualTo("Samsung electronics");
	}
	
	@Test
	public void testDeleteBrand() {
		brandRepository.deleteById(2);
		Optional<Brand> brand = brandRepository.findById(2);
		
		assertThat(brand).isEmpty();
	}
	
}






