package com.shopme.admin.paging;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PagingAndSortingArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		// chỉ support duy nhất 1 parameter được chú thích bằng cách sử dụng @PagingAndSortingParam
		return parameter.getParameterAnnotation(PagingAndSortingParam.class) != null;
	}

	/**
	 * resolveArgument(): sẽ được Spring MVC gọi khi @PagingAndSortingParam được sử dụng trong controller khi xử lý method
	 *
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer model, 
			                 NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {
		
		PagingAndSortingParam annotation = parameter.getParameterAnnotation(PagingAndSortingParam.class);
		// read value của các field và set vào trong model
		String sortDir = request.getParameter("sortDir");
		String sortField = request.getParameter("sortField");
		String keyword = request.getParameter("keyword");
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);	
		model.addAttribute("keyword", keyword);
		// annotation.listName(), annotation.moduleURL(): lấy ra các giá trị của moduleURL, listName của @PagingAndSortingParam được truyền vào từ controller
		model.addAttribute("moduleURL", annotation.moduleURL());
		return new PagingAndSortingHelper(model, annotation.listName(), sortField, sortDir, keyword);
	}

}




