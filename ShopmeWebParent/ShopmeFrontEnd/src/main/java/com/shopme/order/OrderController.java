package com.shopme.order;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.customer.CustomerService;

@Controller 
public class OrderController {
	
	@Autowired private OrderService orderService;
	
	@Autowired private CustomerService customerService;
	
	@GetMapping("/orders")
	public String listByFirstPage(Model model, HttpServletRequest request) {
		return listOrderByPage(model, 1, "orderTime", "desc", null, request);
	}
	
	@GetMapping("/orders/page/{pageNumber}")
	public String listOrderByPage(Model model, @PathVariable("pageNumber") Integer pageNumber,
			         String sortField, String sortDir, String orderKeyword, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		Page<Order> page = orderService.listForCustomerByPage(customer, pageNumber, sortField, sortDir, orderKeyword);
		
		List<Order> listOrders = page.getContent();
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		long startCount = (pageNumber - 1) * OrderService.ORDERS_PER_PAGE + 1;
		long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("orderKeyword", orderKeyword);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("moduleURL", "/orders");
		return "orders/orders_customer";
	}
	
	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(Model model, @PathVariable(name = "id") Integer id, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		Order order = orderService.getOrder(id, customer);		
		model.addAttribute("order", order);
		return "orders/order_details_modal";
	}
	
	// lấy ra 1 đối tượng customer đại diên cho customer đã được xác thực (vì chỉ khi customer đã login mới có thể xem được phần order)
	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);				
		return customerService.getCustomerByEmail(email);
	}	
	
}


