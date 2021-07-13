package com.shopme.admin.order;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.setting.Setting;

@Controller
public class OrderController {
	
	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";  
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private SettingService settingService;
	
	@GetMapping("/orders")
	public String listFirstPage() {
		return defaultRedirectURL;
	}
	
	@GetMapping("/orders/page/{pageNumber}")
	public String listByPage(@PathVariable("pageNumber") int pageNumber, HttpServletRequest request, 
			                 @PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper) {
		orderService.listByPage(pageNumber, helper);
		loadCurrencySetting(request);
		return "orders/orders";
	}
	
	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, Model model, 
			         HttpServletRequest request, RedirectAttributes attributes) {
		try {
			Order order = orderService.getOrder(id);
			loadCurrencySetting(request);
			model.addAttribute("order", order);
			return "orders/order_details_modal";
		} catch (OrderNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;
		}
	}
	
	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable("id") Integer id, RedirectAttributes attributes) {
		try {
			orderService.deleteOrder(id);
			attributes.addFlashAttribute("message", "The order ID " + id + " has been deleted.");
		} catch (OrderNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectURL;
	}
	
	private void loadCurrencySetting(HttpServletRequest request) {
		List<Setting> listSettings = settingService.getCurrencySetting();
		for (Setting setting : listSettings) {
			// put (key và value) vào trong request
			request.setAttribute(setting.getKey(), setting.getValue());
			System.out.println(setting.getKey() + ", " + setting.getValue());
		}
	}
	
}

