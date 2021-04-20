package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)    // keep data ton tai trong DB
public class CategoryRepositoryTest {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Electronics");
		Category savedCategory = categoryRepository.save(category);
		
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateSubCategory() {
		Category parentCategory = new Category(7);
		Category subCategory = new Category("iPhone", parentCategory);
		
		Category savedCategory = categoryRepository.save(subCategory);
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}

	@Test
	public void testGetCategory() {
		// lấy ra tên category cha và các sub-category của nó trong database
		Category category = categoryRepository.findById(1).get();
		System.out.println(category.getName());
		
		Set<Category> children = category.getChildren();
		for(Category subCategory : children) {
			System.out.println(subCategory.getName());
		}
		
		assertThat(children.size()).isGreaterThan(0);
	}
	
	@Test
	public void testPrintHierarchicalCategories() {
		// lấy ra các categories trong DB, xong check nếu category đó k có parent -> là category top-level 
		// từ category top-level đó ta sẽ lấy ra các sub-category của nó, và từ sub-category đó ta sẽ lấy tiếp các sub-sub-category, ...
		Iterable<Category> categories = categoryRepository.findAll();
		for(Category category : categories) {
			if(category.getParent() == null) {
				System.out.println(category.getName());
				Set<Category> children = category.getChildren();
				for(Category subCategory : children) {
					System.out.println("--" + subCategory.getName());
					printChildrenCategory(subCategory, 1);
				}
			}
		}
	}
	
	private void printChildrenCategory(Category parentCategory, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = parentCategory.getChildren();
		for(Category subCategory : children) {
			for(int i = 0; i < newSubLevel; i++) {
				System.out.print("--");
			}
			System.out.println(subCategory.getName());
			// áp dụng tiếp cho sub con của con nữa nếu có(newSubLevel=3)
			printChildrenCategory(subCategory, newSubLevel);
		}
	}
	
	@Test
	public void findRootCategories() {
		List<Category> listRootCategories = categoryRepository.findRootCategories(Sort.by("name").ascending());
		listRootCategories.forEach(category -> System.out.println(category.getName()));
	}
	
	@Test
	public void testFindByName() {
		String name = "Computers";
		Category category = categoryRepository.findByName(name);
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(name);
	}
	
	@Test
	public void testFindByAlias() {
		String alias = "electronics";
		Category category = categoryRepository.findByName(alias);
		assertThat(category).isNotNull();
		assertThat(category.getAlias()).isEqualTo(alias);
	}
}







