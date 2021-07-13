package com.shopme.admin.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.PaymentMethod;
import com.shopme.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderRepositoryTest {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewOrderWithSingleProduct() {
		Customer customer = entityManager.find(Customer.class, 1);
		Product product = entityManager.find(Product.class, 1);
		
		Order mainOrder = new Order();
		mainOrder.setCustomer(customer);
		mainOrder.copyAddressFromCustomer();
		
		mainOrder.setOrderTime(new Date());
		mainOrder.setProductCost(product.getCost());
		mainOrder.setShippingCost(10);
		mainOrder.setTax(0);
		mainOrder.setSubtotal(product.getPrice());
		mainOrder.setTotal(product.getPrice() + 10);
		
		mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
		mainOrder.setStatus(OrderStatus.NEW);
		mainOrder.setDeliverDate(new Date());
		mainOrder.setDeliverDays(1);
		
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setOrder(mainOrder);
		orderDetail.setProduct(product);
		orderDetail.setProductCost(product.getCost());
		orderDetail.setQuantity(1);
		orderDetail.setShippingCost(10);
		orderDetail.setSubtotal(product.getPrice());
		orderDetail.setUnitPrice(product.getPrice());
		
		// put object orderDetail vào mainOrder
		mainOrder.getOrderDetails().add(orderDetail);
		
		Order savedOrder = orderRepository.save(mainOrder);
		assertThat(savedOrder.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateNewOrderWithMultipleProduct() {
		Customer customer = entityManager.find(Customer.class, 10);
		Product product1 = entityManager.find(Product.class, 20);
		Product product2 = entityManager.find(Product.class, 40);
		
		Order mainOrder = new Order();
		mainOrder.setCustomer(customer);
		mainOrder.copyAddressFromCustomer();
		mainOrder.setOrderTime(new Date());
		
		OrderDetail orderDetail1 = new OrderDetail();
		orderDetail1.setOrder(mainOrder);
		orderDetail1.setProduct(product1);
		orderDetail1.setProductCost(product1.getCost());
		orderDetail1.setQuantity(1);
		orderDetail1.setShippingCost(10);
		orderDetail1.setSubtotal(product1.getPrice());
		orderDetail1.setUnitPrice(product1.getPrice());
		
		OrderDetail orderDetail2 = new OrderDetail();
		orderDetail2.setOrder(mainOrder);
		orderDetail2.setProduct(product2);
		orderDetail2.setProductCost(product2.getCost());
		orderDetail2.setQuantity(2);
		orderDetail2.setShippingCost(20);
		orderDetail2.setSubtotal(product2.getPrice() * 2);
		orderDetail2.setUnitPrice(product2.getPrice());
		
		// put 2 object orderDetail1, orderDetail2 vào mainOrder
		mainOrder.getOrderDetails().add(orderDetail1);
		mainOrder.getOrderDetails().add(orderDetail2);
		
		mainOrder.setProductCost(product1.getCost() + product2.getCost());
		mainOrder.setShippingCost(30);
		mainOrder.setTax(0);
		float subtotal = product1.getPrice() + product2.getPrice();
		mainOrder.setSubtotal(subtotal);
		mainOrder.setTotal(subtotal + 30);
		
		mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
		mainOrder.setStatus(OrderStatus.PACKAGED);
		mainOrder.setDeliverDate(new Date());
		mainOrder.setDeliverDays(3);
		
		Order savedOrder = orderRepository.save(mainOrder);
		assertThat(savedOrder.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListOrder() {
		Iterable<Order> orders = orderRepository.findAll();
		orders.forEach(System.out::println);
		
		assertThat(orders).hasSizeGreaterThan(0);
	}
	
	@Test
	public void testUpdateOrder() {
		Integer orderId = 2;
		Order order = orderRepository.findById(orderId).get();
		order.setStatus(OrderStatus.SHIPPING);
		order.setPaymentMethod(PaymentMethod.COD);
		order.setOrderTime(new Date());
		order.setDeliverDays(2);
		
		Order updatedOrder = orderRepository.save(order);
		
		assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPING);
	}
	
	@Test
	public void testGetOrder() {
		Integer orderId = 1;
		Order order = orderRepository.findById(orderId).get();
		
		assertThat(order).isNotNull();
		System.out.println(order);
	}
	
	@Test
	public void testDeleteOrder() {
		Integer orderId = 2;
		orderRepository.deleteById(orderId);
		
		Optional<Order> result = orderRepository.findById(orderId);
		assertThat(result).isNotPresent();
	}
	
}


