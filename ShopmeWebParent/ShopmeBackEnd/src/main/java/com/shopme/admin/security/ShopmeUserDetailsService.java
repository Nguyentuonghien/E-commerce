package com.shopme.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shopme.admin.user.UserRepository;
import com.shopme.common.entity.User;

/**
 * UserDetailsService: là 1 interface trung tâm của spring security, được sử dụng bởi spring security mỗi lần user đăng nhập vào hệ thống
 *                     và có chức năng tìm kiếm tài khoản của user và các vai trò của user đó
 *
 */
public class ShopmeUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.getUserByEmail(email);
		if(user != null) {
			return new ShopmeUserDetails(user);  // nếu tìm thấy nó sẽ tạo một đối tượng User tương ứng(instance của UserDetails)
		}
		throw new UsernameNotFoundException("Could not find user with email: "+email);
	}
	
}
