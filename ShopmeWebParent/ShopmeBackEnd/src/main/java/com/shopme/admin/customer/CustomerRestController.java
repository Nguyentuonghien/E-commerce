package com.shopme.admin.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {
	
	@Autowired
	private CustomerService customerService;
	
	@PostMapping("/customers/check_email")
	public String checkEmailUnique(@RequestParam("id") Integer id, @RequestParam("email") String email) {
		if (customerService.isEmailUnique(email, id)) {
			return "OK";
		} else {
			return "Duplicated";
		}
	}
	
}
