package com.shopme.customer;

import java.io.UnsupportedEncodingException;

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
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.SettingService;


@Controller
public class ForgotPasswordController {
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private SettingService settingService;
	
	@GetMapping("/forgot_password")
	public String showRequestForm() {
		return "customers/forgot_password_form";
	}
	
	@PostMapping("/forgot_password")
	public String processRequestForm(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		try {
			// http://localhost/Shopme/reset_password?token=...
			String token = customerService.updateResetPassword(email);
			String link = Utility.getSiteURL(request) + "/reset_password?token=" + token;
			sendEmail(email, link);
			model.addAttribute("message", "We have sent a reset password link to your email. Please check.");
		} catch (CustomerNotFoundException e) {
			model.addAttribute("error", e.getMessage());
		} catch (UnsupportedEncodingException | MessagingException e) {
			model.addAttribute("error", "Could not send email.");
		} 
		return "customers/forgot_password_form";
	}
	
	@GetMapping("/reset_password")
	public String showResetPasswordForm(@Param("token") String token, Model model) {
		Customer customer = customerService.getByResetPasswordToken(token);
		if (customer != null) {
			model.addAttribute("token", token);
		} else {
			model.addAttribute("pageTitle", "Invalid Token");
			model.addAttribute("title", "Invalid Token!");
			return "message";
		}
		return "customers/reset_password_form";
	}
	
	@PostMapping("/reset_password")
	public String processResetPasswordForm(HttpServletRequest request, Model model) {
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		try {
			customerService.updatePassword(password, token);
			model.addAttribute("title", "Reset Your Password");
			model.addAttribute("message", "You have successfully changed your password.");
			model.addAttribute("pageTitle", "Reset Password");
		} catch (CustomerNotFoundException e) {
			model.addAttribute("pageTitle", "Invalid Token");
			model.addAttribute("message", e.getMessage());
		}
		return "message";
	}
	
	private void sendEmail(String email, String link) throws UnsupportedEncodingException, MessagingException {
		// lấy ra list các đối tượng Setting có category là MAIL_SERVER và MAIL_TEMPLATES trong DB và add vào trong EmailSettingBag
		// để từ EmailSettingBag này ta sẽ lấy ra được các value của từng object Setting tương ứng
		EmailSettingBag emailSettingBag = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettingBag);

		String toAddress = email; 
		String subject = "Here's the link to reset your password.";
		String content = "<p>Hello,</p>"
				       + "<p>You have requested to reset your password.</p>"
				       + "Click the link below to change your password:</p>"
				       + "<p><a href=\"" + link + "\">Change my password</a></p>"
				       + "<br>"
				       + "<p>Ignore this email if you do remember your password, "
				       + "or you have not made the request.</p>";

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

		// set các giá trị: nội dung, tiêu đề, mail được gửi từ đâu đến đâu,... cho messageHelper
		messageHelper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
		messageHelper.setTo(toAddress);
		messageHelper.setSubject(subject);		
		messageHelper.setText(content, true);
		mailSender.send(mimeMessage);
	}
	
}


