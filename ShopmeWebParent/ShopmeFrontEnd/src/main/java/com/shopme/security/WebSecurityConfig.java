package com.shopme.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shopme.security.oauth.CustomerOAuth2UserService;
import com.shopme.security.oauth.OAuth2LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired 
	private CustomerOAuth2UserService oAuth2UserService;
	
	@Autowired
	private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler; 
	
	@Autowired
	private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		       // các url cần xác thực để có thể truy cập được
		       .antMatchers("/account_details", "/update_account_details", "/orders/**", 
						"/cart", "/address_book/**", "/checkout", "/place_order", 
						"/process_paypal_order").authenticated()
		       .anyRequest().permitAll()    
		       .and()
		       .formLogin()
		           // cho phép người dùng xác thực bằng form login với đường dẫn "/login", parameter mặc định email
		           .loginPage("/login")           
		           .usernameParameter("email")    
		           .successHandler(databaseLoginSuccessHandler)
		           .permitAll()
		       .and()
		       .oauth2Login()
		           // kích hoạt xác thực OAuth cùng với form login thông thường
		           .loginPage("/login")
		           .userInfoEndpoint()
		           .userService(oAuth2UserService)
		           .and()
		           .successHandler(oAuth2LoginSuccessHandler)
		       .and()
		       .logout().permitAll()
		       .and()
		       .rememberMe()
		           .key("1234567890_aBcDeFgHiJkLmNoPqRsTuVwXyZ")
		           .tokenValiditySeconds(14 * 24 * 60 * 60)
		       .and()
		           // tạo giá trị mã thông báo CSRF ngay cả khi user không đăng nhập
		           .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);  
	}
	
	@Override
	public void configure(WebSecurity webSecurity) throws Exception {
		webSecurity.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}	
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomerUserDetailsService();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		// Để sử dụng spring security với Spring Data JPA,Hibernate, ta cần cung cấp DaoAuthenticationProvider 
		// yêu cầu UserDetailsService(tìm kiếm user) và PasswordEncoder(so sánh, mã hóa pass) 
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
	
}


