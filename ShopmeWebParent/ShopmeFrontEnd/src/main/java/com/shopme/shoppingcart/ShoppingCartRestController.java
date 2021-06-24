package com.shopme.shoppingcart;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	/**
	 * vì chỉ khi customer login thành công(bằng db hoặc google,facebook) thì mới có thể thực hiện chức năng "Add to Cart"
	 * b1: lấy ra email của customer đã được xác thực, nếu k tìm được customer theo email trong db -> báo lỗi vì chưa login 
	 * b2: nếu tìm thấy email -> sẽ tìm customer trong db theo email và trả về 1 object customer đã được xác thực(đã login)
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




