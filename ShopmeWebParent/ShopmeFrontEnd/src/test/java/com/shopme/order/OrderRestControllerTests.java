package com.shopme.order;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderRestControllerTests {
	
	@Autowired private MockMvc mockMvc;
	
	@Autowired private ObjectMapper objectMapper;
	
	@Test
	@WithUserDetails("tinajamerson1997@gmail.com")
	public void testSendOrderReturnRequestFailed() throws Exception {
		Integer orderId = 111;
		String reason = "";
		String note = "";
		OrderReturnRequest orderReturnRequest = new OrderReturnRequest(orderId, reason, note);
		
		String requestURL = "/orders/return";
		// ta set contentType cho request là json -> trong content của request ta convert object OrderReturnRequest thành chuỗi json
		mockMvc.perform(post(requestURL).with(csrf())
				                        .contentType("application/json")
				                        .content(objectMapper.writeValueAsString(orderReturnRequest)))
		       .andExpect(MockMvcResultMatchers.status().isNotFound())
		       .andDo(print());
	}
	
	@Test
	@WithUserDetails("tinajamerson1997@gmail.com")
	public void testSendOrderReturnRequestSuccessed() throws Exception {
		Integer orderId = 14;
		String reason = "I bought the wrong items";
		String note = "Please return my money.";
		OrderReturnRequest orderReturnRequest = new OrderReturnRequest(orderId, reason, note);
		
		String requestURL = "/orders/return";
		// ta set contentType cho request là json -> trong content của request ta convert object OrderReturnRequest thành chuỗi json
		mockMvc.perform(post(requestURL).with(csrf())
				                        .contentType("application/json")
				                        .content(objectMapper.writeValueAsString(orderReturnRequest)))
		       .andExpect(MockMvcResultMatchers.status().isOk())
		       .andDo(print());
	}
	
}




