package com.shopme.admin.shippingrate;

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
import com.shopme.common.entity.ShippingRate;

@Controller
public class ShippingRateController {
	
	private String defaultRedirectURL = "redirect:/shipping_rates/page/1?sortField=country&sortDir=asc";
	
	@Autowired
	private ShippingRateService shippingRateService;
	
	@GetMapping("/shipping_rates")
	public String listFirstPage() {
		return defaultRedirectURL;
	}
	
	@GetMapping("/shipping_rates/page/{pageNumber}")
	public String listByPage(@PagingAndSortingParam(listName = "shippingRates", moduleURL = "/shipping_rates") PagingAndSortingHelper helper, 
			               @PathVariable("pageNumber") int pageNumber) {
		shippingRateService.listByPage(pageNumber, helper);
		return "shipping_rates/shipping_rates";
	}
	
	@GetMapping("/shipping_rates/new")
	public String newShippingRate(Model model) {
		List<Country> listCountries = shippingRateService.listAllCountries();
		model.addAttribute("shippingRate", new ShippingRate());
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "New Shipping Rate");
		return "shipping_rates/shipping_rate_form";
	}
	
	@PostMapping("/shipping_rates/save")
	public String saveShippingRate(ShippingRate shippingRate, RedirectAttributes attributes) {
		try {
			shippingRateService.save(shippingRate);
			attributes.addFlashAttribute("message", "The shipping rate has been saved successfully.");
		} catch (ShippingRateAlreadyExistsException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectURL;
	}
	
	@GetMapping("/shipping_rates/edit/{id}")
	public String editShippingRate(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
		try {
			ShippingRate shippingRate = shippingRateService.getShippingRate(id);
			List<Country> listCountries = shippingRateService.listAllCountries();
			model.addAttribute("shippingRate", shippingRate);
			model.addAttribute("listCountries", listCountries);
			model.addAttribute("pageTitle", "Edit Rate (ID: " + id + ")");
			return "shipping_rates/shipping_rate_form";
		} catch (ShippingRateNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return "shipping_rates/shipping_rates";
		}
	}
	
	@GetMapping("/shipping_rates/cod/{id}/enabled/{codSupported}")
	public String updateCODSupport(Model model, RedirectAttributes attributes, @PathVariable("id") Integer id,  
			                       @PathVariable("codSupported") Boolean codSupported) {
		try {
			shippingRateService.updateCODSupport(id, codSupported);
			attributes.addFlashAttribute("message", "COD support for shipping rate ID " + id + " has been updated.");
		} catch (ShippingRateNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectURL;
	}
	
	@GetMapping("/shipping_rates/delete/{id}")
	public String deleteShippingRate(@PathVariable("id") Integer id, RedirectAttributes attributes) {
		try {
			shippingRateService.delete(id);
			attributes.addFlashAttribute("message", "The shipping rate with ID " + id + " has been deleted.");
		} catch (ShippingRateNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectURL;
	}
	
}



