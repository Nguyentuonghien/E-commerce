package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	/**
	 * UserDetailsService: Có quyền truy cập vào password của user, trong bảng user có cột username và password, 
	 *                     nơi ta lưu trữ hashed password(pass đã mã hóa) của user -> Spring Security cần bạn xác định hai bean 
	 *                     để thiết lập và chạy authentication là UserDetailsService và PasswordEncoder
	 */
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new ShopmeUserDetailsService();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		// Để sử dụng spring security với Spring Data JPA,Hibernate, 
		// ta cần cung cấp DaoAuthenticationProvider yêu cầu UserDetailsService(tìm kiếm user) và PasswordEncoder(so sánh, mã hóa pass) 
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// cung cap userService vs passEncoder cho spring security
		auth.authenticationProvider(authenticationProvider());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		    .antMatchers("/users/**", "/settings/**", "/countries/**", "/states/**").hasAuthority("Admin")
		    .antMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")
		    .antMatchers("/products/newProduct", "/products/deleteProduct/**").hasAnyAuthority("Admin", "Editor") 
		    .antMatchers("/products/editProduct/**", "/products/saveProduct", "/products/check_unique")
		              .hasAnyAuthority("Admin", "Editor", "Salesperson")
		    .antMatchers("/products", "/products/", "/products/detail/**", "/products/page/**")
		              .hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")         
		    .antMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
		    .antMatchers("/customers/**", "/shipping_rates/**", "/orders/**", "/get_shipping_cost").hasAnyAuthority("Admin", "Salesperson")
		    .anyRequest().authenticated()
		    .and()
		    .formLogin()
		        .loginPage("/login")          // cho phép người dùng xác thực bằng form login với đường dẫn "/login"
		        .usernameParameter("email")   // parameter mac dinh la username-> ta doi thanh email
		        .permitAll() 
		    .and().logout().permitAll()
		    .and()
		        .rememberMe().key("AbcDefgHijKlmnOpqrs_1234567890")   // key(): xác định key để mã hóa cookie được ghi ở browser
		        .tokenValiditySeconds(7 * 24 * 60 * 60);              // tokenValiditySeconds(): sẽ xác định thời gian tồn tại cookies(7 days)
		http.headers().frameOptions().sameOrigin();    // cho phép load iframe của cùng một trang web                   
	}
	
	@Override
	public void configure(WebSecurity webSecurity) throws Exception {
		// Spring Security sẽ bỏ qua các URLs có dạng:
		webSecurity.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}
	
}


