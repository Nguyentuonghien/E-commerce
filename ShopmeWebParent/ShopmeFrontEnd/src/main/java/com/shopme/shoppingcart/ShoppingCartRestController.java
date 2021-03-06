package com.shopme.shoppingcart;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.customer.CustomerService;

@RestController
public class ShoppingCartRestController {
	
	@Autowired
	private ShoppingCartService  cartService;
	
	@Autowired
	private CustomerService customerService;
	
	@PostMapping("/cart/add/{productId}/{quantity}")
	public String addProductToCart(HttpServletRequest request, @PathVariable("productId") Integer productId, 
			                      @PathVariable("quantity") Integer quantity) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			Integer updatedQuantity = cartService.addProduct(productId, quantity, customer);
			return updatedQuantity + " item(s) of this product were  added to your shopping cart.";
		} catch (CustomerNotFoundException e) {
			return "You must login to add this product to cart.";
		} catch (ShoppingCartException e) {
			return e.getMessage();
		}
	}
	
	@PostMapping("/cart/update/{productId}/{quantity}")
	public String updateQuantity(HttpServletRequest request, @PathVariable("productId") Integer productId, 
			                      @PathVariable("quantity") Integer quantity) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			float subTotal = cartService.updateQuantity(quantity, customer, productId);
			return String.valueOf(subTotal);
		} catch (CustomerNotFoundException e) {
			return "You must login to change quantity of product.";
		}
	}
	
	@DeleteMapping("/cart/remove/{productId}")
	public String removeProduct(@PathVariable("productId") Integer productId, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			cartService.removeProduct(customer, productId);
			return "The product has been removed from your shopping cart.";
		} catch (CustomerNotFoundException e) {
			return "You must login to remove product.";
		}
	}
	
	/**
	 * v?? ch??? khi customer login th??nh c??ng(b???ng db ho???c google,facebook) th?? m???i c?? th??? th???c hi???n ch???c n??ng "Add to Cart"
	 * b1: l???y ra email c???a customer ???? ???????c x??c th???c, n???u k t??m ???????c customer theo email trong db -> b??o l???i v?? ch??a login 
	 * b2: n???u t??m th???y email -> s??? t??m customer trong db theo email v?? tr??? v??? 1 object customer ???? ???????c x??c th???c(???? login)
	*/
	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		if (email == null) {
			throw new CustomerNotFoundException("No authenticated customer");
		}
		Customer customer = customerService.getCustomerByEmail(email);
		return customer;
	}
	
}




