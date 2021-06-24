package com.shopme;

import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.shopme.security.oauth.CustomerOAuth2User;
import com.shopme.setting.EmailSettingBag;

public class Utility {
	
	// trả về context path thực của ứng dụng web(http://localhost/Shopme)
	public static String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}
	
	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settingBag) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		// ta cần config một số thuộc tính cho máy chủ SMTP(có thể config trong application configuration file)
		mailSender.setHost(settingBag.getHost());
		mailSender.setPort(settingBag.getPort());
		mailSender.setUsername(settingBag.getUsername());
		mailSender.setPassword(settingBag.getPassword());
		
		// set mail properties
		Properties mailProperties = new Properties();
		mailProperties.setProperty("mail.smtp.auth", settingBag.getSmtpAuth());
		mailProperties.setProperty("mail.smtp.starttls.enable", settingBag.getSmtpSecured());
		mailSender.setJavaMailProperties(mailProperties);
		
		return mailSender;
	}
	
	// lấy ra email của customer đã được xác thực, vì có 1 số cách login khác nhau: login = form(có hoặc không dùng remember me) -> trả về email của customer 
	// còn login = google or facebook sẽ trả về name customer -> từ đối tượng principal ta sẽ cast theo từng trường hợp tương ứng để
	public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		// customer chưa login -> null
		if (principal == null) return null;
		String customerEmail = null;
		if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
			customerEmail = request.getUserPrincipal().getName();
		} else if (principal instanceof OAuth2AuthenticationToken){
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User customerOAuth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
			customerEmail = customerOAuth2User.getEmail();
		}
		return customerEmail;
	}
	
}
