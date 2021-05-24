package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;

/**
 * Transaction: là 1 giao dịch (1 giao tác) bao gồm 1 loạt các hành động được phải được thực hiện thành công cùng nhau,
 *              nếu 1 hành động thất bại thì tất cả các hành động trong loạt hành động đó sẽ trở về trạng thái ban đầu
 * @Transactional : Spring cung cấp cơ chế hỗ trợ quản lý transaction tự động start, commit, hay rollback transaction tự động.
 *                  Nếu đặt @Transaction ở đầu class thì tất cả các method trong class đó đều nằm trong 1 transaction,
 *                  Nếu đặt @Transaction ở đầu method thì chỉ các method đó được nằm trong 1 transaction.
 *
 */

@Service
@Transactional
public class CategoryService {
	
	public static final int ROOT_CATEGORIES_PER_PAGE = 4;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	// trước khi save category ta check xem category đó có Parent không? nếu có -> lấy ra allParentIDs của parent đó
	public Category save(Category category) {
		Category parentCategory = category.getParent();
		if (parentCategory != null) {
			String allParentIDs = parentCategory.getAllParentIDs() == null ? "-" : parentCategory.getAllParentIDs(); // "-" or "-1-"
			allParentIDs += String.valueOf(parentCategory.getId() + "-");   // "-1-" or "-1-5-"
			category.setAllParentIDs(allParentIDs);
		}
		return categoryRepository.save(category);
	}
	
	public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNumber, String sortDir, String keyword) {
		Sort sort = Sort.by("name");
		if (sortDir.equals("asc")) {
			sort = sort.ascending();
		} else if (sortDir.equals("desc")) {
			sort = sort.descending();
		}		
		Pageable pageable = PageRequest.of(pageNumber - 1,  ROOT_CATEGORIES_PER_PAGE, sort);
		Page<Category> pageCategories = null;
		// nếu search -> lấy toàn bộ các đối tượng là root-category(parent_id=null) trong DB theo keyword và phân trang 
		// còn nếu k search thì chỉ lấy ra và phân trang cho root-category
		if (keyword != null && !keyword.isEmpty()) {
			pageCategories = categoryRepository.searchCategories(keyword, pageable);
		} else {
			pageCategories =categoryRepository.findRootCategories(pageable);
		}		
		List<Category> rootCategories = pageCategories.getContent();
		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());
		
		// nếu search thì kết quả trả về không hiển thị dạng phân cấp(không có --, ----) còn không search vẫn trả về như thường(có phân cấp)
		if (keyword != null && !keyword.isEmpty()) {
			List<Category> searchResult = pageCategories.getContent();
			for (Category category : searchResult) {
				category.setHasChildren(category.getChildren().size() > 0);
			}
			return searchResult;
		} else {
			return listHierarchicalCategories(rootCategories, sortDir);
		}
	}
	
	private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
		List<Category> hierarchicalCategories = new ArrayList<>();
		for (Category rootCategory : rootCategories) {
			// gán toàn bộ các field cho rootCategory vì ta cần hiển thị các thông tin trên trang categories(gồm id, image, name, alias, enabled)
			hierarchicalCategories.add(Category.copyFull(rootCategory));
			// sắp xếp sub-category cho list categories
			Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);
			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));
				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
			}
		}
		return hierarchicalCategories;
	}
	
	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parentCategory, int subLevel, String sortDir) {
		int newSubLevel = subLevel + 1;
		// sắp xếp sub-category cho list categories
		Set<Category> children = sortSubCategories(parentCategory.getChildren(), sortDir);
		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			hierarchicalCategories.add(Category.copyFull(subCategory, name));
			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
		}
	}
	
	// for form Add and Edit
	public List<Category> listCategoriesUsedInForm() {	
		List<Category> categoriesUsedInForm = new ArrayList<>();
		Iterable<Category> rootCategoriesInDB = categoryRepository.findRootCategories(Sort.by("name").ascending());	
		for (Category category : rootCategoriesInDB) {
			// gán id và name cho parent-category(form chỉ cần có id và name của parent để ta chọn)
			categoriesUsedInForm.add(Category.copyIdAndName(category));
			// sắp xếp sub-category cho form
			Set<Category> children = sortSubCategories(category.getChildren());
			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();
				// gán id và name cho sub-category(form chỉ cần có id và "--"+name của sub để ta chọn) 
				categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
				listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
			}
		}
     	return categoriesUsedInForm;
	}

	private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int sublevel) {
		int newSubLevel = sublevel + 1;
		// sắp xếp sub-category cho form
		Set<Category> children = sortSubCategories(parent.getChildren());		
		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
			listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
		}
	}
	
	// hàm sắp xếp các sub-categories cho form(theo thứ tự asc) 
	private SortedSet<Category> sortSubCategories(Set<Category> children) {
		return sortSubCategories(children, "asc");
	}	
		
	// hàm sắp xếp các sub-categories cho list categories(theo thứ tự asc hoặc desc) 
	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
			@Override
			public int compare(Category category1, Category category2) {
				if (sortDir.equals("asc")) {
					return category1.getName().compareTo(category2.getName());
				} else {
					return category2.getName().compareTo(category1.getName());
				}
			}
		});
		sortedChildren.addAll(children);
		return sortedChildren;
	}
	
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);
		Category categoryByName = categoryRepository.findByName(name);
		if (isCreatingNew) {
			if (categoryByName != null) {
				return "Duplicate Name";
			} else {
				Category categoryByAlias = categoryRepository.findByAlias(alias);
				if (categoryByAlias != null) {
					return "Duplicate Alias";
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "Duplicate Name";
			}
			Category categoryByAlias = categoryRepository.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "Duplicate Alias";
			}
		}
		return "OK";
	}
	
	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		categoryRepository.updateEnabledStatus(id, enabled);
	}
	
	public Category getCategory(Integer id) throws CategoryNotFoundException {
		try {
			return categoryRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CategoryNotFoundException("Could not find any category with ID " +id);
		}
	}
	
	public void deleteCategory(Integer id) throws CategoryNotFoundException {
		Long countById = categoryRepository.countById(id);
		if (countById == null || countById == 0) {
			throw new CategoryNotFoundException("Could not find any category with ID: " + id);
		}
		categoryRepository.deleteById(id);
	}
			
	
}





