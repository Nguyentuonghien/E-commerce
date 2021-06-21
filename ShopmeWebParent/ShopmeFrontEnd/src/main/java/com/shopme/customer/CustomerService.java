package com.shopme.customer;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
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
	
	public Customer getCustomerByEmail(String email) {
		return customerRepository.findByEmail(email);
	}
	
	public void registerCustomer(Customer customer) {
		encodePassword(customer);
		// customer không thể login nếu chưa kích hoạt tài khoản bằng cách kiểm tra email và nhấp vào liên kết xác minh được nhúng trong email.
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(AuthenticationType.DATABASE);
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		System.out.println("Verification Code: " + randomCode);
		customerRepository.save(customer);
	}
	
	public void updateCustomer(Customer customerInForm) {
		Customer customerInDB = customerRepository.findById(customerInForm.getId()).get();
		// chỉ khi login bằng DB mới có thể update password còn login = google, facebook thì không
		if (customerInDB.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
			if (!customerInForm.getPassword().isEmpty()) {
				String passwordEncoded = passwordEncoder.encode(customerInForm.getPassword());
				customerInForm.setPassword(passwordEncoded);
			} else {
				customerInForm.setPassword(customerInDB.getPassword());
			}
		} else {
			customerInForm.setPassword(customerInDB.getPassword());
		}
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());
		customerRepository.save(customerInForm);
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
	
	public String updateResetPassword(String email) throws CustomerNotFoundException {
		// tìm customer trong db theo email đã nhập trong form, nếu tìm được sẽ sinh ra 1 token random cho customer đó và trả về token đó 
		// để gắn vào link xác thục trong e-mail
		Customer customer = customerRepository.findByEmail(email);
		if (customer != null) {
			String token = RandomString.make(30);
			customer.setResetPasswordToken(token);
			customerRepository.save(customer);
			return token;
		} else {
			throw new CustomerNotFoundException("Could not find any customer with email " + email);
		}
	}
	
	public Customer getByResetPasswordToken(String token) {
		return customerRepository.findByResetPasswordToken(token);
	}
	
	public void updatePassword(String newPassword, String token) throws CustomerNotFoundException {
		Customer customer = customerRepository.findByResetPasswordToken(token);
		if (customer != null) {
			customer.setPassword(newPassword);
			customer.setResetPasswordToken(null);
			encodePassword(customer);
			customerRepository.save(customer);
		} else {
			throw new CustomerNotFoundException("No customer found: invalid token");
		}
	}
	
	public void updateCustomerAuthenticationType(Customer customer, AuthenticationType type) {
		// nếu type khác kiểu authentication type của customer -> update 
		if (!customer.getAuthenticationType().equals(type)) {
			customerRepository.updateAuthenticationType(customer.getId(), type);
		}
	}
	
	public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode, AuthenticationType authenticationType) {
		Customer customer = new Customer();
		customer.setEmail(email);
		setName(name, customer);
		customer.setEnabled(true);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(authenticationType);
		customer.setPassword("");      // login qua google k can password
		customer.setAddressLine1("");
		customer.setCity("");
		customer.setState("");
		customer.setPhoneNumber("");
		customer.setPostalCode("");
		customer.setCountry(countryRepository.findByCode(countryCode));
		
		customerRepository.save(customer);
	}
	
	private void encodePassword(Customer customer) {
		String encodedPassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
	}
	
	private void setName(String name, Customer customer) {
		// tách chuỗi name(gồm firstName, lastName) thành 1 mảng các chuỗi con theo khoảng trắng nameArray[] = {firstName, lastName};
		String[] nameArray = name.split(" ");  
		if (nameArray.length < 2) {   
			customer.setFirstName(name);
			customer.setLastName("");
		} else {
			String firstName = nameArray[0];
			customer.setFirstName(firstName);
			String lastName = name.replaceFirst(firstName + " ", "");
			customer.setLastName(lastName);
		}
	}
	
}



