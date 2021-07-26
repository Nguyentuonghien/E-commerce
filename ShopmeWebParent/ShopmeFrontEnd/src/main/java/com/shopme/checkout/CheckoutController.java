package com.shopme.checkout;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.Utility;
import com.shopme.address.AddressService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.order.PaymentMethod;
import com.shopme.customer.CustomerService;
import com.shopme.order.OrderService;
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
		model.addAttribute("checkoutInfo", checkoutInfo);
		model.addAttribute("listCartItems", listCartItems);
		return "checkout/checkout";
	}
	
	@PostMapping("/place_order")
	public String placeOrder(HttpServletRequest request) {
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
		
		orderService.createOrder(customer, defaultAddress, listCartItems, checkoutInfo, paymentMethod);
		// sau khi 1 order được tạo -> ta sẽ empty shopping cart của customer đã xác thực đó
		shoppingCartService.deteleByCustomer(customer);
		return "checkout/order_completed";
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		return customerService.getCustomerByEmail(email);
	}
	
}


