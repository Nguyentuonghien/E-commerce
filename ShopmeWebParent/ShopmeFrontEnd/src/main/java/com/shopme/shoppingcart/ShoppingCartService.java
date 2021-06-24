package com.shopme.shoppingcart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;

@Service
public class ShoppingCartService {

	@Autowired
	private CartItemRepository cartItemRepository;

	public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
		Integer updatedQuantity = quantity;
		Product product = new Product(productId);
		CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);
		
		// tìm cartItem theo customer và product trong db. Nếu cartItem == null -> product này chưa được mua -> tạo 1 cartItem mới và set các gtri mới cho nó 
		// nếu cartItem != null -> update số lượng cho nó = số lượng hiện tại trong cart + số lượng thêm vào và số lượng k được > 5
		if (cartItem == null) {
			cartItem = new CartItem();
			cartItem.setCustomer(customer);
			cartItem.setProduct(product);
			System.out.println("With cartItem == null, Updated Quantity = " + updatedQuantity);
		} else {
			updatedQuantity = cartItem.getQuantity() + quantity;
			System.out.println("With cartItem != null, Updated Quantity = " + updatedQuantity);
			if (updatedQuantity > 5) {
				throw new ShoppingCartException("Could not add more " + quantity + " item(s)"
						+ " because there's already " + cartItem.getQuantity() + " item(s) "
						+ "in your shopping cart. Maximum allowed quantity is 5.");
			}
		}	
		cartItem.setQuantity(updatedQuantity);
		cartItemRepository.save(cartItem);
		return updatedQuantity;
	}

}

 

