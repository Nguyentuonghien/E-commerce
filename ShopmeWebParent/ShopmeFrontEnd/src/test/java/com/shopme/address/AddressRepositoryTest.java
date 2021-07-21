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
		Integer countryId = 234;
		
		Address newAddress = new Address();
		newAddress.setCustomer(new Customer(customerId));
		newAddress.setCountry(new Country(countryId));
		newAddress.setFirstName("Charle");
		newAddress.setLastName("Burger");
		newAddress.setPhoneNumber("0915344779");
		newAddress.setAddressLine1("41/27 Vong Street");
		newAddress.setAddressLine2("");
		newAddress.setCity("Ha Noi");
		newAddress.setState("Ha Noi");
		newAddress.setPostalCode("123456");
		
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
	
	@Test
	public void testSetDefaultAddress() {
		Integer addressId = 4;
		addressRepository.setDefaultAddress(addressId);
		Address address = addressRepository.findById(addressId).get();
		
		assertThat(address.isDefaultForShipping()).isTrue();
	}
	
	@Test
	public void testSetNonDefaultAddress() {
		Integer addressId = 4;
		Integer customerId = 5;
		addressRepository.setNonDefaultAddressForOthers(addressId, customerId);
	}
	
	@Test
	public void testGetDefault() {
		Integer customerId = 5;
		Address address = addressRepository.findDefaultAddressByCustomer(customerId);
		
		assertThat(address).isNotNull();
		System.out.println(address);
	}
	
}

