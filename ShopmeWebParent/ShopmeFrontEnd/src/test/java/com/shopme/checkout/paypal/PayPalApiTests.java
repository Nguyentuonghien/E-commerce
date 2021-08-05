package com.shopme.checkout.paypal;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class PayPalApiTests {
	
	private static final String BASE_URL = "https://api.sandbox.paypal.com";
	private static final String GET_ORDER_API = "/v2/checkout/orders/";
	private static final String CLIENT_ID = "AaLizebpXjNoop7ZRBbO-fWFNHkV6GdqQe3kZ_J_LCQrqnkDqfXdNlsTdMjMx7Fq697aH6xKSwn798R5";
	private static final String CLIENT_SECRET = "EPutFQNBO5qmKgm69jfYnulWhT15mJWz-SKABhrSzm8JAqDmR4eyRlNOTmG37Q3kjUHDXKLDi4NXGE6y";
	
	/**
	 * RestTemplate là một thành phần cốt lõi Spring Framework cho phép thực hiện các cuộc gọi đồng bộ (synchronous calls) bởi client
	 * để truy cập vào RESTful Web Service
	 */
	@Test
	public void testGetOrderDetails() {
		// connect to paypal api server to get the order details by orderId
		String orderId = "108178106W6957247";
		String requestURL = BASE_URL + GET_ORDER_API + orderId;
		
		HttpHeaders headers = new HttpHeaders();
		// for api call paypal server, the code accept response in JSON format(yêu cầu trả về định dạng JSON)
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Language", "en_US");
		headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();
		// Thực hiện phương thức GET và trả về phản hồi ResponseEntity lầ 1 đối tượng PaypalOrderResponse
		ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
		PayPalOrderResponse paypalOrderResponse = response.getBody();
		
		System.out.println("Order ID: " + paypalOrderResponse.getId());
		System.out.println("Validated: " + paypalOrderResponse.validate(orderId));
	}
	
}



