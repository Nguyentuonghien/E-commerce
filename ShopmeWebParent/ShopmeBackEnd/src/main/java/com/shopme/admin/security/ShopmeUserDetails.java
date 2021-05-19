package com.shopme.admin.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;


/**
 * UserDetails: là 1 interface của spring security để chứa toàn bộ thông tin về user và cung cấp các phương thức để truy cập đến các thông tin cơ bản của user
 * getAuthorities(): trả về danh sách các quyền(roles) của user<lấy ra list các role name của user>
 * getPassword(): trả về password đẫ dùng trong quá trình xác thực(lấy pass của user)
 * getUsername(): trả về username đã dùng trong quá trình xác thực(lấy username của user)
 */

public class ShopmeUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private User user;
		
	public ShopmeUserDetails(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Role> roles = user.getRoles();
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for(Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}
	
	@Override
	public boolean isAccountNonExpired() {
		// trả về true nếu tài khoản của người dùng chưa hết hạn
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// trả về true nếu người dùng chưa bị khóa
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// trả về true nếu chứng thực (mật khẩu) của người dùng chưa hết hạn
		return true;
	}

	public String getFullname() {
		return this.user.getFirstName() + " " + this.user.getLastName();
	}
	
	public void setFirstName(String firstName) {
		this.user.setFirstName(firstName);
	}

	public void setLastName(String lastName) {
		this.user.setLastName(lastName);
	}	
	
	public boolean hasRole(String roleName) {
		return user.hasRole(roleName);
	}
	
}



