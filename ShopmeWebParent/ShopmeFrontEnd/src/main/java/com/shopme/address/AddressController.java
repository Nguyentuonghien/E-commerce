package com.shopme.address;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;

@Controller
public class AddressController {
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/address_book")
	public String showAddressBook(Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		List<Address> listAddresses = addressService.listAddressBook(customer);
		
		// nếu address đã có defaultForShipping(1) -> sẽ được dùng làm primary address
		boolean usePrimaryAddressAsDefault = true;
		for (Address address : listAddresses) {
			if (address.isDefaultForShipping()) {
				usePrimaryAddressAsDefault = false;
				break;
			}
		}
		model.addAttribute("listAddresses", listAddresses);
		model.addAttribute("customer", customer);
		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
		return "address_book/addresses";
	}
	
	@GetMapping("/address_book/new")
	public String newAddress(Model model) {
		List<Country> listCountries = customerService.listAllCountries();
		model.addAttribute("address", new Address());
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Add New Address");
		return "address_book/address_form";
	}
	
	@PostMapping("/address_book/save")
	public String saveAddress(Address address, RedirectAttributes attributes, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		address.setCustomer(customer);
		addressService.saveAddressBook(address);
		attributes.addFlashAttribute("message", "The address has been saved successfully.");
		return "redirect:/address_book";
	}
	
	@GetMapping("/address_book/edit/{id}")
	public String editAddress(@PathVariable("id") Integer id, Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		Address address = addressService.getAddressBook(id, customer.getId());
		List<Country> listCountries = customerService.listAllCountries();
		model.addAttribute("address", address);
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Edit Address (ID: " + id + ")");
		return "address_book/address_form";
	}
	
	@GetMapping("/address_book/delete/{id}")
	public String deleteAddress(@PathVariable("id") Integer id, HttpServletRequest request, RedirectAttributes attributes) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.deleteAddressBook(id, customer.getId());
		attributes.addFlashAttribute("message", "The address ID " + id + " has been deleted.");
		return "redirect:/address_book";
	}
	
	@GetMapping("/address_book/default/{id}")
	public String setDefaultAddress(@PathVariable("id") Integer addressId, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.setDefaultAddress(addressId, customer.getId());
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";
		if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		}
		return redirectURL;
	}
	
	// lấy ra 1 đối tượng customer đại diên cho customer đã được xác thực (vì chỉ khi customer đã login mới có thể xem được phần address-book)
	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		Customer customer = customerService.getCustomerByEmail(email);
		return customer;
	}
	
}
