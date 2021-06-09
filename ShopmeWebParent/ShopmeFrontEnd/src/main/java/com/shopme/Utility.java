package com.shopme;

import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
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
	
}
