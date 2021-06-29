package com.shopme.admin.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;

@Controller
public class CustomerController {
	
	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/customers")
	public String listFirstPage(Model model) {
		return "redirect:/customers/page/1?sortField=firstName&sortDir=asc";
	}
	
	@GetMapping("/customers/page/{pageNumber}")
	public String listByPage(@PathVariable("pageNumber") int pageNumber,
			      @PagingAndSortingParam(listName = "listCustomers", moduleURL = "/customers") PagingAndSortingHelper helper) {
		customerService.listByPage(pageNumber, helper);
		return "customers/customers";
	}
	
	@PostMapping("/customers/saveCustomer") 
	public String saveCustomer(Customer customer, RedirectAttributes attributes) {
		customerService.saveCustomer(customer);
		attributes.addFlashAttribute("message", "The Customer ID " + customer.getId() + " has been saved successfully.");
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/editCustomer/{id}")
	public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
		try {
			Customer customer = customerService.getById(id);
			List<Country> listCountries = customerService.listAllCountries();
			model.addAttribute("customer", customer);
			model.addAttribute("listCountries", listCountries);
			model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", id));
			return "customers/customer_form";
		} catch (CustomerNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/customers";
		}
	}
	
	@GetMapping("/customers/{id}/enabled/{status}")
	public String updateEnabledStatus(@PathVariable("status") boolean enabled, 
			@PathVariable("id") Integer id, RedirectAttributes attributes) {
		customerService.updateCustomerEnabledStatus(enabled, id);
		String status = enabled ? "enabled" : "disabled";
		String message = "The Customer ID " + id +" has been " + status;
		attributes.addFlashAttribute("message", message);
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/detail/{id}")
	public String viewDetailCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
		try {
			Customer customer = customerService.getById(id);
			model.addAttribute("customer", customer);
			return "customers/customer_detail_modal";
		} catch (CustomerNotFoundException ex) {
			attributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/customers";
		}
	}
		
	@GetMapping("/customers/deleteCustomer/{id}")
	public String deleteCustomer(@PathVariable("id") Integer id, 
			    RedirectAttributes attributes) {
		try {
			customerService.deleteCustomer(id);
			attributes.addFlashAttribute("message", "The customer ID " + id + " has been deleted successfully.");
		} catch (CustomerNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/customers";
	}
	
}

