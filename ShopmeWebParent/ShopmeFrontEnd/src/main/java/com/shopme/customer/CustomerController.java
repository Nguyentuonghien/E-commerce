package com.shopme.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.Utility;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;

@Controller
public class CustomerController {
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private SettingService settingService;
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		List<Country> listCountries = customerService.listAllCountries();
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Customer Registration");
		model.addAttribute("customer", new Customer());
		return "register/register_form";
	}
	
	@PostMapping("/create_customer")
	public String createCustomer(Customer customer, Model model, HttpServletRequest request) 
			        throws UnsupportedEncodingException, MessagingException {
		// sau khi customer đăng kí thành công -> send email xác minh cho customer đó
		customerService.registerCustomer(customer);
		sendVerificationEmail(customer, request);
		
		model.addAttribute("pageTitle", "Registration Succeeded!");
		return "register/register_success";
	}

	private void sendVerificationEmail(Customer customer, HttpServletRequest request)
			    throws UnsupportedEncodingException, MessagingException {
		// lấy ra list các đối tượng Setting có category là MAIL_SERVER và MAIL_TEMPLATES trong DB và add vào trong EmailSettingBag
		// để từ EmailSettingBag này ta sẽ lấy ra được các value của từng object Setting tương ứng
		EmailSettingBag emailSettingBag = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettingBag);
		 
		String toAddress = customer.getEmail(); 
		String subject = emailSettingBag.getCustomerVerifySubject();
		String content = emailSettingBag.getCustomerVerifyContent();
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
		
		// set các giá trị: nội dung, tiêu đề, mail được gửi từ đâu đến đâu,... cho messageHelper
		messageHelper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
		messageHelper.setTo(toAddress);
		messageHelper.setSubject(subject);		
		content = content.replace("[[name]]", customer.getFullName());
		// verifyURL có dạng http://localhost/Shopme/verify?code=...
		String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();
		content = content.replace("[[URL]]", verifyURL);
		messageHelper.setText(content, true);
		mailSender.send(mimeMessage);
		
		System.out.println("to Address: " + toAddress);
		System.out.println("Verify URL:" + verifyURL);
	}

	@GetMapping("/verify")
	public String verifiAccount(@Param("code") String code, Model model) {
		boolean verified = customerService.verifyAccount(code);
		return "register/" + (verified ? "verify_success" : "verify_fail");
	}
	
}





