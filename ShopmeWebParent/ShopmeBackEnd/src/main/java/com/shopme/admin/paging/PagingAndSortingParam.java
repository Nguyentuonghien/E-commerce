package com.shopme.admin.paging;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Retention(RUNTIME): Mức tồn tại lớn nhất, được bộ dịch (compiler) nhận biết, và máy ảo (jvm) cũng nhận ra khi chạy chương trình.
 * @Target(PARAMETER) : Cho phép chú thích trên parameter
 *
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface PagingAndSortingParam {
	
	public String moduleURL();
	public String listName();
	
}
