package com.shopme.customer;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.setting.CountryRepository;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public List<Country> listAllCountries() {
		List<Country> listCountries = countryRepository.findAllByOrderByNameAsc();
		return listCountries;
	}
	
	public boolean isEmailUnique(String email) {
		Customer customer = customerRepository.findByEmail(email);
		return customer == null;
	}
	
	public void registerCustomer(Customer customer) {
		encodePassword(customer);
		// customer không thể login nếu chưa kích hoạt tài khoản bằng cách kiểm tra email và nhấp vào liên kết xác minh được nhúng trong email.
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		System.out.println("Verification Code: " + randomCode);
		customerRepository.save(customer);
	}
	
	private void encodePassword(Customer customer) {
		String encodedPassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
	}
	
	public boolean verifyAccount(String verificationCode) {
		// nếu k tìm thấy customer or customer đã được enbled -> false vì customer chưa được verify thì enabled=false 
		// và có verificationCode sau khi verify -> ta set enabled=true và verificationCode=null
		Customer customer = customerRepository.findByVerificationCode(verificationCode);
		if (customer == null || customer.isEnabled()) {
			return false;
		} else {
			customerRepository.enableCustomer(customer.getId());
			return true;
		}	
	}
	
}


