package com.shopme.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopme.common.entity.Category;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	public List<Category> listNoChildrenCategories() {
		List<Category> listNoChildrenCategories = new ArrayList<>();
		List<Category> listEnabledcategories = categoryRepository.findAllCategoriesEnabled();
		listEnabledcategories.forEach(category -> {
			// check category phải không có sub-categories
			Set<Category> children = category.getChildren();
			if (children == null || children.size() == 0) {
				listNoChildrenCategories.add(category);
			}
		});
		return listNoChildrenCategories;
	}
	
	public Category findByAliasEnabled(String alias) {
		return categoryRepository.findByAliasEnabled(alias);
	}
	
	public List<Category> getParentsCategory(Category children) {
		List<Category> listParents = new ArrayList<>();
		
		// dùng while để lấy ra các parent của parent... của category đã cho
		// vd: category là Graphic Cards -> ta lấy các parents category của nó : Computer Components, Computers
		Category parentCategory = children.getParent();
		while (parentCategory != null) {
			listParents.add(0, parentCategory);
			parentCategory = parentCategory.getParent();
		}
		listParents.add(children);
		return listParents;
	}
	
}





