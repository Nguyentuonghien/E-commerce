package com.shopme.order;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.common.exception.OrderNotFoundException;
import com.shopme.customer.CustomerService;

@RestController
public class OrderRestController {
	
	@Autowired private OrderService orderService;
	
	@Autowired private CustomerService customerService;
	
	
	@PostMapping("/orders/return")
	public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest orderReturnRequest , HttpServletRequest request) {
		System.out.println("Order ID: " + orderReturnRequest.getOrderId());
		System.out.println("Reason: " + orderReturnRequest.getReason());
		System.out.println("Notes: " + orderReturnRequest.getNote());
		
		Customer customer = null;
		try {
			customer = getAuthenticatedCustomer(request);
		} catch (CustomerNotFoundException e) {
			return new ResponseEntity<>("Authentication required", HttpStatus.BAD_REQUEST);
		}
		try {
			orderService.setOrderReturnRequested(orderReturnRequest, customer);
		} catch (OrderNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new OrderReturnResponse(orderReturnRequest.getOrderId()), HttpStatus.OK);
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		if (email == null) {
			throw new CustomerNotFoundException("No authenticated customer.");
		}
		Customer customer = customerService.getCustomerByEmail(email);
		return customer;
	}
	
}
