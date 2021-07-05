package com.shopme.address;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTest {
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Test
	public void testAddNew() {
		Integer customerId = 5;
		Integer countryId = 234; // USA
		
		Address newAddress = new Address();
		newAddress.setCustomer(new Customer(customerId));
		newAddress.setCountry(new Country(countryId));
		newAddress.setFirstName("Tobie");
		newAddress.setLastName("Abel");
		newAddress.setPhoneNumber("19094644165");
		newAddress.setAddressLine1("4213 Gordon Street");
		newAddress.setAddressLine2("Novak Building");
		newAddress.setCity("Chino");
		newAddress.setState("California");
		newAddress.setPostalCode("91710");
		
		Address savedAddress = addressRepository.save(newAddress);
		
		assertThat(savedAddress).isNotNull();
		assertThat(savedAddress.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testFindByCustomer() {
		Integer customerId = 5;
		List<Address> addresses = addressRepository.findByCustomer(new Customer(customerId));
		addresses.forEach(System.out::println);
		
		assertThat(addresses.size()).isGreaterThan(0);
	}
	
	@Test
	public void testFindByIdAndCustomer() {
		Integer customerId = 5;
		Integer addressId = 1;
		Address address = addressRepository.findByIdAndCustomer(addressId, customerId);
		
		assertThat(address).isNotNull();
		System.out.println(address);
	}
	
	@Test
	public void testUpdate() {
		Integer addressId = 2;
		// String phoneNumber = "646-232-3932";
		
		Address address = addressRepository.findById(addressId).get();
		//address.setPhoneNumber(phoneNumber);
		address.setDefaultForShipping(true);

		Address updatedAddress = addressRepository.save(address);
		//assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
	}
	
	@Test
	public void testDeleteByIdAndCustomer() {
		Integer customerId = 5;
		Integer addressId = 1;
		addressRepository.deleteByIdAndCustomer(addressId, customerId);
		Address address = addressRepository.findByIdAndCustomer(addressId, customerId);
		
		assertThat(address).isNull();
	}
	
}

