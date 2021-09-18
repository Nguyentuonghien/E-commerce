package com.shopme.order;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.OrderTrack;
import com.shopme.common.entity.order.PaymentMethod;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.OrderNotFoundException;

@Service
public class OrderService {
	
	public static final int ORDERS_PER_PAGE = 3;
	
	@Autowired
	private OrderRepository orderRepository;
	
	public Order createOrder(Customer customer, Address address, List<CartItem> cartItems, CheckoutInfo checkoutInfo, PaymentMethod paymentMethod) {
		Order newOrder = new Order();
		newOrder.setOrderTime(new Date());
		if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
			newOrder.setStatus(OrderStatus.PAID);
		} else {
			newOrder.setStatus(OrderStatus.NEW);
		}
		newOrder.setCustomer(customer);
		newOrder.setProductCost(checkoutInfo.getProductCost());
		newOrder.setSubtotal(checkoutInfo.getProductTotal());
		newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
		newOrder.setTotal(checkoutInfo.getPaymentTotal());
		newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
		newOrder.setDeliverDate(checkoutInfo.getDeliverDate());
		newOrder.setTax(0.0f);
		newOrder.setPaymentMethod(paymentMethod);
		if (address == null) {
			// address của người nhận giống với address của customer
			newOrder.copyAddressFromCustomer();
		} else {
			// address của người nhận khác với address của customer
			newOrder.copyShippingAddress(address);
		}
		
		// khi set status cho newOrder phía trên là NEW -> ta cũng set cho orderTrack với status là NEW
		List<OrderTrack> orderTracks = newOrder.getOrderTracks();
		OrderTrack orderTrack = new OrderTrack();
		orderTrack.setOrder(newOrder);
		orderTrack.setOrderStatus(OrderStatus.NEW);
		orderTrack.setNotes(OrderStatus.NEW.defaultDescription());
		orderTrack.setUpdatedTime(new Date());
		orderTracks.add(orderTrack);
		
		Set<OrderDetail> orderDetails = newOrder.getOrderDetails();
		for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrder(newOrder);
			orderDetail.setProduct(product);
			orderDetail.setQuantity(cartItem.getQuantity());
			orderDetail.setUnitPrice(product.getDiscountPrice());
			orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
			orderDetail.setSubtotal(cartItem.getSubtotal());
			orderDetail.setShippingCost(cartItem.getShippingCost());
			orderDetails.add(orderDetail);
		}
		return orderRepository.save(newOrder);
	}
	
	public Page<Order> listForCustomerByPage(Customer customer, int pageNumber, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNumber - 1, ORDERS_PER_PAGE, sort);
		Page<Order> page = null;
		if (keyword != null) {
			page = orderRepository.findAll(keyword, customer.getId(), pageable);
		} else {
			page = orderRepository.findAll(customer.getId(), pageable);
		}
		return page;
	}
	
	public Order getOrder(Integer id, Customer customer) {
		return orderRepository.findByIdAndCustomer(id, customer);
	}
	
	public void setOrderReturnRequested(OrderReturnRequest orderReturnRequest, Customer customer) throws OrderNotFoundException {
		Order order = orderRepository.findByIdAndCustomer(orderReturnRequest.getOrderId(), customer);
		if (order == null) {
			throw new OrderNotFoundException("Could not found any order with ID: " + orderReturnRequest.getOrderId());
		}
		if (order.isReturnRequested()) {
			return;
		}
		// nếu order chưa có status là return_request
		List<OrderTrack> orderTracks = order.getOrderTracks();
		OrderTrack orderTrack = new OrderTrack();
		orderTrack.setOrder(order);
		orderTrack.setUpdatedTime(new Date());
		orderTrack.setOrderStatus(OrderStatus.RETURN_REQUESTED);
		
		String notes = "Reason: " + orderReturnRequest.getReason();
		if (!orderReturnRequest.getNote().equals("")) {
			notes += ". " + orderReturnRequest.getNote();
		}
		orderTrack.setNotes(notes);
		
		orderTracks.add(orderTrack);
		order.setStatus(OrderStatus.RETURN_REQUESTED);
		
		orderRepository.save(order);
	}
	
}


