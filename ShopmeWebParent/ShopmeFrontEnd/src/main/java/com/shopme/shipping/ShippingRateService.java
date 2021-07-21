package com.shopme.shipping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;

@Service
public class ShippingRateService {
	
	@Autowired
	private ShippingRateRepository shippingRateRepository;
	
	public ShippingRate getShippingRateForCustomer(Customer customer) {
		String state = customer.getState();
		// vì state của customer không bắt buộc phải có, nếu không có thì ta sẽ thay = city của customer
		if (state == null || state.isEmpty()) {
			state = customer.getCity();
		}
		return shippingRateRepository.findByCountryAndState(customer.getCountry(), state);
	}
	
	public ShippingRate getShippingRateForAddress(Address address) {
		String state = address.getState();
		// vì state của address không bắt buộc phải có, nếu không có thì ta sẽ thay = city của address
		if (state == null || state.isEmpty()) {
			state = address.getCity();
		}
		return shippingRateRepository.findByCountryAndState(address.getCountry(), state);
	}
	
}
