package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.common.entity.Category;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {
	
	@MockBean
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CategoryService categoryService;
	
	@Test
	public void testCheckUniqueInNewModeReturnDuplicateName() {
		Integer id = null;
		String name = "Computers";
		String alias = "abc";
		
		Category category = new Category(id, name, alias);
		
		// trả về đối tượng category khi gọi hàm findByName(name) và category đó sẽ có name là Computers
		// và trả về null khi gọi hàm findByAlias() vì k có alias nào có tên là abc
		Mockito.when(categoryRepository.findByName(name)).thenReturn(category);
		Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(null);
		
		String result = categoryService.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("Duplicate Name");
	}
	
	@Test
	public void testCheckUniqueInNewModeReturnDuplicateAlias() {
		Integer id = null;
		String name = "abc";
		String alias = "computers";
		
		Category category = new Category(id, name, alias);
		
		// trả về đối tượng category khi gọi hàm findByAlias(alias) và category đó sẽ có alias là computers và
		// và trả về null khi gọi hàm findByName(name) vì k có name nào có tên là abc
		Mockito.when(categoryRepository.findByName(name)).thenReturn(null);
		Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(category);
		
		String result = categoryService.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("Duplicate Alias");
	}
	
	@Test
	public void testCheckUniqueInNewModeReturnOK() {
		Integer id = null;
		String name = "abc";
		String alias = "house";
		
		// trả về đối tượng category khi gọi hàm findByName()
		Mockito.when(categoryRepository.findByName(name)).thenReturn(null);
		Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(null);
		
		String result = categoryService.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("OK");
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnDuplicateName() {
		Integer id = 1;
		String name = "Computers";
		String alias = "abc";
		
		Category category = new Category(2, name, alias);
		
		// trả về đối tượng category khi gọi hàm findByName(name) và category đó sẽ có name là Computers và id=2
		// và trả về null khi gọi hàm findByAlias() vì k có alias nào có tên là abc
		Mockito.when(categoryRepository.findByName(name)).thenReturn(category);
		Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(null);
		
		String result = categoryService.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("Duplicate Name");
	}

	@Test
	public void testCheckUniqueInEditModeReturnDuplicateAlias() {
		Integer id = 1;
		String name = "abc";
		String alias = "computers";
		
		Category category = new Category(2, name, alias);
		
		// trả về đối tượng category khi gọi hàm findByAlias(alias) và category đó sẽ có alias là computers và id=2
		// và trả về null khi gọi hàm findByName(name) vì k có name nào có tên là abc
		Mockito.when(categoryRepository.findByName(name)).thenReturn(null);
		Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(category);
		
		String result = categoryService.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("Duplicate Alias");
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnOK() {
		Integer id = 1;
		String name = "abc";
		String alias = "computes";
		
		Category category = new Category(id, name, alias);
		
		// trả về đối tượng category khi gọi hàm findByName()
		Mockito.when(categoryRepository.findByName(name)).thenReturn(null);
		Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(category);
		
		String result = categoryService.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("OK");
	}
	
}



