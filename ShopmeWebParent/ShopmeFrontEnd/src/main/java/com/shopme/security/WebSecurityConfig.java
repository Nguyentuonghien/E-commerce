package com.shopme.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		       .anyRequest().permitAll();
	}
	
	@Override
	public void configure(WebSecurity webSecurity) throws Exception {
		// Spring Security sẽ bỏ qua các URLs có dạng:
		webSecurity.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}
	
}


