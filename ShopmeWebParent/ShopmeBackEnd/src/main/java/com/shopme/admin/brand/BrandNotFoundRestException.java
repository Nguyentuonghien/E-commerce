package com.shopme.admin.brand;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @ResponseStatus: Sử dụng annotation @ResponseStatus trước class định nghĩa exception sẽ chỉ dẫn HTTP Code trả về khi exception này xảy ra
 *                  exception BrandNotFoundRestException  sẽ trả về trang error 404 với message là “Brand not found”.
 *
 */

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Brand not found")
public class BrandNotFoundRestException extends Exception {

	private static final long serialVersionUID = 1L;

}
