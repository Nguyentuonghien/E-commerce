package com.shopme.shoppingcart;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.product.Product;
import com.shopme.product.ProductRepository;

@Service
@Transactional
public class ShoppingCartService {

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private ProductRepository productRepository;
	
	public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
		Integer updatedQuantity = quantity;
		Product product = new Product(productId);
		CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);
		
		// tìm cartItem theo customer,product trong db.Nếu cartItem == null -> product này chưa có trong giỏ hàng -> tạo 1 cartItem mới và set các gtri mới cho nó 
		// nếu cartItem != null -> đã có product trong giỏ hàng -> update số lượng cho nó = số lượng hiện tại trong cart + số lượng thêm vào(số lượng k được > 5)
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
					                + " because there's already " + cartItem.getQuantity() + " item(s) in your shopping cart. Maximum allowed quantity is 5.");
			}  
		}	
		cartItem.setQuantity(updatedQuantity);
		cartItemRepository.save(cartItem);
		return updatedQuantity;
	}

	public List<CartItem> listCartItems(Customer customer) {
		return cartItemRepository.findByCustomer(customer);
	}
	
	public float updateQuantity(Integer quantity, Customer customer, Integer productId) {
		cartItemRepository.updateQuantity(quantity, customer.getId(), productId);
		Product product = productRepository.findById(productId).get();
		float subTotal = product.getDiscountPrice() * quantity;
		return subTotal;
	}
	
	public void removeProduct(Customer customer, Integer productId) {
		cartItemRepository.deleteByCustomerAndProduct(customer.getId(), productId);
	}
	
	public void deteleByCustomer(Customer customer) {
		cartItemRepository.deleteByCustomer(customer.getId());
	}
	
}

 

