package com.shopme.admin.shippingrate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.admin.product.ProductRepository;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.product.Product;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class ShippingRateServiceTests {
	
	@MockBean
	private ShippingRateRepository shippingRateRepository;
	
	@MockBean
	private ProductRepository productRepository;
	
	@InjectMocks
	private ShippingRateService shippingRateService;
	
	@Test
	public void testCalculateShippingCost_NoRateFound() {
		Integer productId = 1;
		Integer countryId = 1234;
		String state = "ABCDE";
		
		Mockito.when(shippingRateRepository.findByCountryAndState(countryId, state)).thenReturn(null);
		assertThrows(ShippingRateNotFoundException.class, new Executable() {
			
			@Override
			public void execute() throws Throwable {
				shippingRateService.calculateShippingCost(productId, countryId, state);
			}
		});
	}	
	
	@Test
	public void testCalculateShippingCost_RateFound() throws ShippingRateNotFoundException {
		Integer productId = 1;
		Integer countryId = 1234;
		String state = "New York";
		
		ShippingRate shippingRate = new ShippingRate();
		shippingRate.setRate(10);
		
		Mockito.when(shippingRateRepository.findByCountryAndState(countryId, state)).thenReturn(shippingRate);
		
		Product product = new Product();
		product.setWeight(5);
		product.setWidth(4);
		product.setHeight(3);
		product .setLength(8);
		Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		
		float shippingCost = shippingRateService.calculateShippingCost(productId, countryId, state);
		
		assertEquals(50, shippingCost);
	}
	
	
}
