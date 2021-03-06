package com.shopme.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.security.CustomerUserDetails;
import com.shopme.security.oauth.CustomerOAuth2User;
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

	@GetMapping("/verify")
	public String verifiAccount(@RequestParam("code") String code, Model model) {
		boolean verified = customerService.verifyAccount(code);
		return "register/" + (verified ? "verify_success" : "verify_fail");
	}
	
	@GetMapping("/account_details")
	public String viewAccountDetails(Model model, HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		Customer customer = customerService.getCustomerByEmail(email);
		List<Country> listCountries = customerService.listAllCountries();
		model.addAttribute("customer", customer);
		model.addAttribute("listCountries", listCountries);
		return "customers/account_form";
	}
	
	@PostMapping("/update_account_details")
	public String updateAccountDetails(Customer customer, RedirectAttributes attributes, HttpServletRequest request) {
		customerService.updateCustomer(customer);
		updateNameAuthenticatedCustomer(customer, request);
		attributes.addFlashAttribute("message", "Your account details have been updated.");
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/account_details";
		// customer đang edit account_detail từ form của address_book -> redirect về trang address_book
		// còn không vẫn redirect về trang account_detail của customer như thường
		// nếu redirect=cart -> return trang shopping_cart
		if (redirectOption.equals("address_book")) {
			redirectURL = "redirect:/address_book";
		} else if (redirectOption.equals("cart")) {
			redirectURL = "redirect:/cart";
		} else if (redirectOption.equals("checkout")) {
			redirectURL = "redirect:/address_book?redirect=checkout";
		}
		return redirectURL;
	}
	
	private void updateNameAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
			CustomerUserDetails customerUserDetails = getCustomerUserDetails(principal);
			customerUserDetails.setFirstName(customer.getFirstName());
			customerUserDetails.setLastName(customer.getLastName());
		} else if (principal instanceof OAuth2AuthenticationToken){
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User customerOAuth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
			customerOAuth2User.setFullName(customer.getFullName());
		}
	}
	
	// cast principal thành CustomerUserDetails khi login bằng db
	private CustomerUserDetails getCustomerUserDetails(Object principal) {
		CustomerUserDetails customerUserDetails = null;
		if (principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) principal;
			customerUserDetails = (CustomerUserDetails) usernamePasswordAuthenticationToken.getPrincipal();
		} else if (principal instanceof RememberMeAuthenticationToken) {
			RememberMeAuthenticationToken rememberMeAuthenticationToken = (RememberMeAuthenticationToken) principal;
			customerUserDetails = (CustomerUserDetails) rememberMeAuthenticationToken.getPrincipal();
		}
		return customerUserDetails;
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
	
}





