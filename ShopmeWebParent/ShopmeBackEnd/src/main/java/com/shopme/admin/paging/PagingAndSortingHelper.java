package com.shopme.admin.paging;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PagingAndSortingHelper {
	
	private ModelAndViewContainer model;
	private String listName;
	private String sortField;
	private String sortDir;
	private String keyword;
	
	public PagingAndSortingHelper(ModelAndViewContainer model, String listName, String sortField, 
			  String sortDir, String keyword) {
		this.model = model;
		this.listName = listName;
		this.sortField = sortField;
		this.sortDir = sortDir;
		this.keyword = keyword;
	}

	public String getSortField() {
		return sortField;
	}

	public String getSortDir() {
		return sortDir;
	}

	public String getKeyword() {
		return keyword;
	}
	
	public void listEntities(int pageNumber, int pageSize, SearchRepository<?, Integer> repository) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
		// nếu search thì sẽ vừa seach+phân trang, nếu k chỉ phân trang
		Page<?> page = null;
		if (keyword != null) {
			page = repository.findAll(keyword, pageable);
		} else {
		    page = repository.findAll(pageable);
		}
		updateModelAttributes(pageNumber, page);
	}
	
	public void updateModelAttributes(int pageNumber, Page<?> pages) {
        List<?> listItems = pages.getContent();
		int pageSize = pages.getSize();
		long startCount = (pageNumber - 1) * pageSize + 1;
		long endCount = startCount + pageSize - 1;
		if(endCount > pages.getTotalElements()) {
			endCount = pages.getTotalElements();
		}
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalPages", pages.getTotalPages());
		model.addAttribute("totalItems", pages.getTotalElements());
		model.addAttribute(listName, listItems);
	}
	
}




