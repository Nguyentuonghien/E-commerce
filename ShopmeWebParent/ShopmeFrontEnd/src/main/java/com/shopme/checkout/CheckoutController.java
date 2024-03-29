package com.shopme.checkout;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.Utility;
import com.shopme.address.AddressService;
import com.shopme.checkout.paypal.PayPalApiException;
import com.shopme.checkout.paypal.PayPalService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.PaymentMethod;
import com.shopme.customer.CustomerService;
import com.shopme.order.OrderService;
import com.shopme.setting.CurrencySettingBag;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.PaymentSettingBag;
import com.shopme.setting.SettingService;
import com.shopme.shipping.ShippingRateService;
import com.shopme.shoppingcart.ShoppingCartService;

@Controller
public class CheckoutController {
	
	@Autowired private CheckoutService checkoutService;
	@Autowired private ShoppingCartService shoppingCartService;
	@Autowired private ShippingRateService shippingRateService;
	@Autowired private CustomerService customerService;
	@Autowired private AddressService addressService;	
	@Autowired private OrderService orderService;
	@Autowired private SettingService settingService;
	@Autowired private PayPalService paypalService;
	
	@GetMapping("/checkout")
	public String showCheckoutPage(Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		ShippingRate shippingRate = null;
		Address defaultAddress = addressService.getDefaultAddress(customer);
		if (defaultAddress != null) {
			shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
			model.addAttribute("shippingAddress", defaultAddress.toString());
		} else {
			shippingRate = shippingRateService.getShippingRateForCustomer(customer);
			model.addAttribute("shippingAddress", customer.toString());
		}
		if (shippingRate == null) {
			return "redirect:/cart";
		}
		List<CartItem> listCartItems = shoppingCartService.listCartItems(customer);
		CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(listCartItems, shippingRate);
		String currencyCode = settingService.getCurrencyCode();
		PaymentSettingBag paymentSettingBag = settingService.getPaymentSettings();
		String paypalClientId = paymentSettingBag.getClientID();
		
		model.addAttribute("checkoutInfo", checkoutInfo);
		model.addAttribute("listCartItems", listCartItems);
		model.addAttribute("customer", customer);
		model.addAttribute("currencyCode", currencyCode);
		model.addAttribute("paypalClientId", paypalClientId);
		return "checkout/checkout";
	}
	
	@PostMapping("/place_order")
	public String placeOrder(HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		Customer customer = getAuthenticatedCustomer(request);
		// get value của form payment bên phía checkout
		String paymentType = request.getParameter("paymentMethod");
		PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);
		
		ShippingRate shippingRate = null;
		Address defaultAddress = addressService.getDefaultAddress(customer);
		if (defaultAddress != null) {
			shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
		} else {
			shippingRate = shippingRateService.getShippingRateForCustomer(customer);
		}
		List<CartItem> listCartItems = shoppingCartService.listCartItems(customer);
		CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(listCartItems, shippingRate);
		
		Order createdOrder = orderService.createOrder(customer, defaultAddress, listCartItems, checkoutInfo, paymentMethod);
		// sau khi 1 order được tạo -> ta sẽ empty shopping cart của customer đã xác thực đó và gửi 1 mail xác nhận order cho customer
		shoppingCartService.deteleByCustomer(customer);
		sendOrderConfirmationEmail(request, createdOrder);
		return "checkout/order_completed";
	}
	
	@PostMapping("/process_paypal_order")
	public String processPayPalOrder(HttpServletRequest request, Model model) throws UnsupportedEncodingException, MessagingException {
		// khi giao dịch hoàn thành(customer ấn PayNow) -> PayPal sẽ trả về OrderID và TotalAmount cho checkout.html
		// ta sẽ lấy ra orderId đó từ checkout page để validating PayPal Order
        String orderId = request.getParameter("orderId");
		String pageTitle = "Checkout Failure";
		String message = null;
		try {
			// nếu true -> paypal order đã được verified -> call tới placeOrder() để save order detail vào trong DB và send email xác minh cho customer
			if (paypalService.validateOrder(orderId)) {
				return placeOrder(request);
			} else {
				pageTitle = "Checkout Failure";
				message = "ERROR: Transaction could not be completed because order information is invalid";
			}
		} catch (PayPalApiException e) {
			message = "ERROR: Transaction failed due to error: " + e.getMessage();
		}
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("title", pageTitle);
		model.addAttribute("message", message);
		return "message";
	}
	
	private void sendOrderConfirmationEmail(HttpServletRequest request, Order createdOrder) throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		mailSender.setDefaultEncoding("utf-8");
		
		String toAddress = createdOrder.getCustomer().getEmail();
		String subject = emailSettings.getOrderConfirmationSubject();
		String content = emailSettings.getOrderConfirmationContent();
		
		subject = subject.replace("[[orderId]]", String.valueOf(createdOrder.getId()));
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
		messageHelper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		messageHelper.setTo(toAddress);
		messageHelper.setSubject(subject);
		
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss E, dd MM yyyy");
		String orderTime = dateFormat.format(createdOrder.getOrderTime());		
		
		CurrencySettingBag currencySettings = settingService.getCurrencySettings();
		String totalAmount = Utility.formatCurrency(createdOrder.getTotal(), currencySettings);
		
		content = content.replace("[[name]]", createdOrder.getCustomer().getFullName());
		content = content.replace("[[orderId]]", String.valueOf(createdOrder.getId()));
		content = content.replace("[[orderTime]]", orderTime);
		content = content.replace("[[shippingAddress]]", createdOrder.getShippingAddress());
		content = content.replace("[[total]]", totalAmount);
		content = content.replace("[[paymentMethod]]", String.valueOf(createdOrder.getPaymentMethod()));
		messageHelper.setText(content, true);
		
		mailSender.send(mimeMessage);
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		return customerService.getCustomerByEmail(email);
	}
	
}






