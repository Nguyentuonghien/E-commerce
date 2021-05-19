package com.shopme.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {
	
	@Autowired
	private CategoryRepository cateRepo;
	
	@Test
	public void testListEnabledCategories() {
		List<Category> categories = cateRepo.findAllCategoriesEnabled();
		categories.forEach(category -> {
			System.out.println(category.getName() + "( " + category.isEnabled() +" )");
		});
	}
	
	@Test
	public void testFindCategoryByAlias() {
		String alias = "camera";
		Category category = cateRepo.findByAliasEnabled(alias);
		
		assertThat(category).isNotNull();
	}
	
}
