package com.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 38);
		Category category = entityManager.find(Category.class, 6);
		Product product = new Product();
		
		product.setName("Acer Aspire Desktop");
		product.setAlias("acer");
		product.setShortDescription("A good desktop");
		product.setFullDescription("Best desktop of the world from USA");
		
		product.setBrand(brand);
		product.setCategory(category);
		
		product.setPrice(900);
		product.setCost(600);
		product.setEnabled(true);
		product.setInStock(true);
		
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		Product savedProduct = productRepository.save(product);
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllProducts() {
		Iterable<Product> products  = productRepository.findAll();
		products.forEach(System.out::println);
	}
	
	@Test
	public void testGetProduct() {
		Integer id = 2;
		Product product = productRepository.findById(id).get();
		System.out.println(product);
		
		assertThat(product).isNotNull();
	}
	
	@Test
	public void testUpdateProduct() {
		Product product = productRepository.findById(1).get();
		product.setPrice(999);
		productRepository.save(product);
		Product updatedProduct = entityManager.find(Product.class, 1);
		
		assertThat(updatedProduct.getPrice()).isEqualTo(999);
	}
	
	@Test
	public void testDeleteProduct() {
		Integer id = 3;
		productRepository.deleteById(id);
		Optional<Product> result = productRepository.findById(id);
		
		assertThat(!result.isPresent());
	}
	
	@Test
	public void testSaveProductWithImages() {
		Product product = productRepository.findById(1).get();
		product.setMainImage("main htrang.png");
		product.addExtraImage("extra_image1.png");
		product.addExtraImage("extra_image_2.png");
		product.addExtraImage("extra-image3.png");
		Product savedProduct = productRepository.save(product);
		
		assertThat(savedProduct.getProductImages().size()).isEqualTo(3);
	}
	
	@Test
	public void testSaveProductDetail() {
		Product product = productRepository.findById(1).get();
		product.addDetail("Device Memory", "128GB");
		product.addDetail("CPU Model", "Media Tech");
		product.addDetail("OS", "Android 10");
		Product savedProduct = productRepository.save(product);
		
		assertThat(savedProduct.getProductDetails()).isNotEmpty();
	}
	
}




