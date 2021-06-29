package com.shopme.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;

@Service
@Transactional
public class CustomerService {
	
	public static final int CUSTOMERS_PER_PAGE = 10;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	public void listByPage(int pageNumber, PagingAndSortingHelper helper) {
		helper.listEntities(pageNumber, CUSTOMERS_PER_PAGE, customerRepository);
	}
	
	public void saveCustomer(Customer customerInForm) {
		// nếu customer nhập pass mới vào trong form(add)->encode cho pass mới đó còn nếu pass trống->k thay đổi pass(edit)->giữ nguyên pass trong DB cho customer
		Customer customerInDB = customerRepository.findById(customerInForm.getId()).get();
		if (!customerInForm.getPassword().isEmpty()) {
			String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(encodedPassword);
		} else {
			customerInForm.setPassword(customerInDB.getPassword());
		}
		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());
		customerRepository.save(customerInForm);
	}
	
	public Customer getById(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CustomerNotFoundException("Could not find any customers with ID: " + id);
		}
	}
	
	public boolean isEmailUnique(String email, Integer id) {
		Customer existCustomer = customerRepository.findByEmail(email);
		// tìm được 1 customer khác có cung email trong DB -> lỗi 
		if (existCustomer != null && existCustomer.getId() != id) {
			return false;
		}
		return true;
	}
	
	public void updateCustomerEnabledStatus(boolean enabled, Integer id) {
		customerRepository.updateEnabledStatus(enabled, id);
	}
	
	public void deleteCustomer(Integer id) throws CustomerNotFoundException {
		Long countById = customerRepository.countById(id);
		if (countById == null || countById == 0) {
			throw new CustomerNotFoundException("Could not find any customers with ID: " + id);
		}
	    customerRepository.deleteById(id);
	}
	
}




