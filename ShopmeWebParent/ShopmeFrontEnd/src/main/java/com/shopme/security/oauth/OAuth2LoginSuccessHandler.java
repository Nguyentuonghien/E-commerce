package com.shopme.security.oauth;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	@Autowired
	private CustomerService customerService;
	
	/**
	 *  onAuthenticationSuccess(): xử lý một số lôgic sau khi đăng nhập thành công bằng Google ví dụ: cập nhật thông tin người dùng trong cơ sở dữ liệu
	 *  và sẽ được Spring OAuth2 gọi khi login thành công bằng Google, vì vậy tại đây ta có thể thực hiện các lôgic tùy chỉnh
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			                  Authentication authentication) throws ServletException, IOException {
		CustomerOAuth2User customerOAuth2User = (CustomerOAuth2User) authentication.getPrincipal();
		String name = customerOAuth2User.getName();
		String email = customerOAuth2User.getEmail();
		String countryCode = request.getLocale().getCountry();
		String clientName = customerOAuth2User.getClientName();
		
		System.out.println("OAuth2LoginSuccessHandler: " + name + " | " + email);	
		System.out.println("Client Name: " + clientName);
		
		// sau khi customer login = Google thành công, ta sẽ có được name, email của customer đó, sau đó sẽ tìm trong DB theo email có được
		// nếu k tìm được customer theo email trong DB -> add customer detail mới vào DB với email, name được sử dụng khi login = Google, 
		// còn nếu tìm được sẽ update customerAuthemticationType = GOOGLE cho customer
		AuthenticationType authenticationType = getAuthenticationType(clientName);
		Customer customer = customerService.getCustomerByEmail(email);
		if (customer == null) {
			customerService.addNewCustomerUponOAuthLogin(name, email, countryCode, authenticationType);
		} else {
			customerService.updateCustomerAuthenticationType(customer, authenticationType);
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
	private AuthenticationType getAuthenticationType(String clientName) {
		if (clientName.equals("Google")) {
			return AuthenticationType.GOOGLE;
		} else if (clientName.equals("Facebook")) {
			return AuthenticationType.FACEBOOK;
		} else {
			return AuthenticationType.DATABASE;
		}
	}
	
}


