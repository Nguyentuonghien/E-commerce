package com.shopme.admin.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderRestController {
	
	@Autowired
	private OrderService orderService;
	
	@PostMapping("/orders_shipper/update/{id}/{status}")
	public Response updateOrderStatus(@PathVariable("id") Integer orderId, @PathVariable("status") String status) {
		orderService.updateStatus(orderId, status);
		// return đối tượng Response và sẽ được chuyển đổi thành chuỗi JSON tương ứng ở phía client
		return new Response(orderId, status);
	}
	
	// Nested class
	class Response {
		private Integer orderId;
		private String status;
		
		public Response(Integer orderId, String status) {
			this.orderId = orderId;
			this.status = status;
		}

		public Integer getOrderId() {
			return orderId;
		}

		public void setOrderId(Integer orderId) {
			this.orderId = orderId;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}
	
}
