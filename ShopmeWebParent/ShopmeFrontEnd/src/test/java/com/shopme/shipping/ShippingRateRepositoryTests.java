package com.shopme.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ShippingRateRepositoryTests {
	
	@Autowired
	private ShippingRateRepository shippingRateRepository;
	
	@Test
	public void testFindByCountryAndState() {
		Country usa = new Country(234);
		String state = "New York";
		ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(usa, state);
		
		assertThat(shippingRate).isNotNull();
		System.out.println(shippingRate);
	}
	
}
