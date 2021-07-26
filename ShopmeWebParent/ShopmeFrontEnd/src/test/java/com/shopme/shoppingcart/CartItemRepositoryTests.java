package com.shopme.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CartItemRepositoryTests {
	
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	public void testSaveItem() {
		Integer customerId = 1;
		Integer productId = 1;
		// lấy ra đối tượng customer và product trong db qua id bằng entityManager
		Customer customer = entityManager.find(Customer.class, customerId);
		Product product = entityManager.find(Product.class, productId);
		
		CartItem cartItem = new CartItem();
		cartItem.setCustomer(customer);
		cartItem.setProduct(product);
		cartItem.setQuantity(10);
		CartItem savedCartItem = cartItemRepository.save(cartItem);
		
		assertThat(savedCartItem.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testSave2Items() {
		Integer customerId = 10;
		Integer productId = 10;
		Customer customer = entityManager.find(Customer.class, customerId);
		Product product = entityManager.find(Product.class, productId);
		
		CartItem item1 = new CartItem();
		item1.setCustomer(customer);
		item1.setProduct(product);
		item1.setQuantity(2);
		
		CartItem item2 = new CartItem();
		item2.setCustomer(new Customer(customerId));
		item2.setProduct(new Product(8));
		item2.setQuantity(3);
		
		Iterable<CartItem> iterable = cartItemRepository.saveAll(Arrays.asList(item1, item2));
		
		assertThat(iterable).size().isGreaterThan(0);
	}
	
	@Test
	public void findByCustomer() {
		Customer customer = entityManager.find(Customer.class, 10);
		List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);
		cartItems.forEach(System.out::println);
		
		assertThat(cartItems.size()).isEqualTo(2);
	}
	
	@Test
	public void findByCustomerAndProduct() {
		Customer customer = entityManager.find(Customer.class, 10);
		Product product = entityManager.find(Product.class, 8);
		CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);
		System.out.println(cartItem);
		
		assertThat(cartItem).isNotNull();
	}
	
	@Test
	public void testUpdateQuantity() {
		Integer customerId = 1;
		Integer productId = 1;
		Integer quantity = 4;
		
		cartItemRepository.updateQuantity(quantity, customerId, productId);
		
		CartItem item = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));
		
		assertThat(item.getQuantity()).isEqualTo(4);
	}
	
	
	@Test
	public void testDeleteByCustomerAndProduct() {
		Integer customerId = 10;
		Integer productId = 10;
		cartItemRepository.deleteByCustomerAndProduct(customerId, productId);
		CartItem item = cartItemRepository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));
		
		assertThat(item).isNull();
	}
	
	@Test
	public void testDeleteByCustomer() {
		Integer customerId = 40;
		cartItemRepository.deleteByCustomer(customerId);
		List<CartItem> cartItems = cartItemRepository.findByCustomer(new Customer(customerId));
		
		assertThat(cartItems.size()).isEqualTo(0);
	}
	
}


