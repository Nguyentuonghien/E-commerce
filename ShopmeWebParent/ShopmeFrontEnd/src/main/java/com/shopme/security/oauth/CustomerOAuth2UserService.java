package com.shopme.security.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomerOAuth2UserService extends DefaultOAuth2UserService {
	
	/**
	 * loadUser(): sẽ được Spring OAuth2 gọi khi xác thực thành công và nó trả về một đối tượng CustomOAuth2User mới.
	 */
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// lấy ra tên của client mà ta chọn để login (VD: Facebook or Google,...)
		String clientName = userRequest.getClientRegistration().getClientName();
		OAuth2User user = super.loadUser(userRequest);
		return new CustomerOAuth2User(user, clientName);
	}
	
}
