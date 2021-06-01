package com.shopme.setting;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shopme.common.entity.Setting;

@Component
public class SettingFilter implements Filter {

	@Autowired
	private SettingService settingService;
	
	/**
	 * doFilter(): được gọi khi một request phù hợp được gửi đến server, được sử dụng để chặn yêu cầu và chuyển đổi phản hồi
	 *
	 */
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {	
		// read url từ request
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String url = servletRequest.getRequestURL().toString();
		// những url có đuôi như sau thì không cần filter -> cho phép request được đi tiếp(vượt qua filter này)
		if (url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg")) {
			chain.doFilter(request, response); 
			return;
		}
		List<Setting> listSettings = settingService.getGeneralSettings();
		listSettings.forEach(setting -> {
			System.out.println(setting);
			// put (value và key) vào trong request
			request.setAttribute(setting.getKey(), setting.getValue());
		});
		chain.doFilter(request, response); 
	}

}



