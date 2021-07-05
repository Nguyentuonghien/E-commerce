package com.shopme.address;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;

@Service
@Transactional
public class AddressService {
	
	@Autowired
	private AddressRepository addressRepository;
	
	public List<Address> listAddressBook(Customer customer) {
		return addressRepository.findByCustomer(customer);
	}
	
	public void saveAddressBook(Address addressInForm) {
		addressRepository.save(addressInForm);
	}
	
	public Address getAddressBook(Integer addressId, Integer customerId) {
		return addressRepository.findByIdAndCustomer(addressId, customerId);
	}
	
	public void deleteAddressBook(Integer addressId, Integer customerId) {
		addressRepository.deleteByIdAndCustomer(addressId, customerId);
	}
	
}


