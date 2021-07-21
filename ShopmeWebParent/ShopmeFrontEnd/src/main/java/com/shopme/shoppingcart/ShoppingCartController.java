package com.shopme.shoppingcart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.Utility;
import com.shopme.address.AddressService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.customer.CustomerService;
import com.shopme.shipping.ShippingRateService;

@Controller
public class ShoppingCartController {
	
	@Autowired
	private ShoppingCartService shoppingCartService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private ShippingRateService shippingRateService;
	
	@GetMapping("/cart")
	public String viewCart(Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		List<CartItem> listCartItems = shoppingCartService.listCartItems(customer);
		float estimatedTotal = 0.0F;
		for (CartItem item : listCartItems) {
			estimatedTotal += item.getSubtotal();
		}
		
		// lấy ra default address của customer được xác thực, nếu default address của customer khác null -> lấy ra shipping rate theo default address đó
		// còn không lấy shipping rate theo địa chỉ customer(primary address), nếu shipping rate != null -> address của customer đó được support
		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		boolean usePrimaryAddressAsDefault = false;
		if (defaultAddress != null) {
			shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
		} else {
			shippingRate = shippingRateService.getShippingRateForCustomer(customer);
			usePrimaryAddressAsDefault = true;
		}
		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
		model.addAttribute("shippingSupported", shippingRate != null);
		model.addAttribute("listCartItems", listCartItems);
		model.addAttribute("estimatedTotal", estimatedTotal);
		return "cart/shopping_cart";
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		Customer customer = customerService.getCustomerByEmail(email);
		return customer;
	}
	
}





