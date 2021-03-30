package com.shopme.admin.user.export;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;

public class AbstractExporter {
	
	public void setResponseHeader(HttpServletResponse response, String contentType, String extension) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String timeFormat = dateFormat.format(new Date());
		String fileName = "users_" + timeFormat + extension;
		response.setContentType(contentType);
		
		// Để gửi dữ liệu cho users dưới dạng file download, ta cần đặt tiêu đề cho response 
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + fileName;
		response.setHeader(headerKey, headerValue);
	}
	
}
