package com.shopme.admin.order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.OrderTrack;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.setting.Setting;

@Controller
public class OrderController {
	
	// mặc định sort theo orderTime giảm dần
    private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";  
	
	@Autowired private OrderService orderService;
	
	@Autowired private SettingService settingService;
	
	@GetMapping("/orders")
	public String listFirstPage() {
		return defaultRedirectURL;
	}
	
	@GetMapping("/orders/page/{pageNumber}")
	public String listByPage(@PathVariable("pageNumber") int pageNumber, HttpServletRequest request, 
			                 @PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper,
			                 @AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		orderService.listByPage(pageNumber, helper);
		loadCurrencySetting(request);
		// user đã login mà chỉ có role là "shipper"
		if (loggedUser.hasRole("Shipper") && !loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson")) {
			return "orders/orders_shipper";
		}
		return "orders/orders";
	}
	
	@PostMapping("/orders/save")
	public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes attributes) {
		// khi submit form -> get countryName từ request mà ta đã gán cho countryName của hidden bên order_form_shipping.html và gán lại cho order(là kiểu string)
		String countryName = request.getParameter("countryName");
		order.setCountry(countryName);
		updateProductDetails(order, request);
		updateOrderTracks(order, request);
		orderService.save(order);
		attributes.addFlashAttribute("message", "The order ID " + order.getId() + " has been saved successfully.");
		return defaultRedirectURL;
	}

	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, Model model, HttpServletRequest request, 
			            @AuthenticationPrincipal ShopmeUserDetails loggedUser, RedirectAttributes attributes) {
		try {
			Order order = orderService.getOrder(id);
			loadCurrencySetting(request);
			boolean isVisibleForAdminOrSalesperson = false;
			if (loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson")) {
				isVisibleForAdminOrSalesperson = true;
			}
			model.addAttribute("isVisibleForAdminOrSalesperson", isVisibleForAdminOrSalesperson);
			model.addAttribute("order", order);
			return "orders/order_details_modal";
		} catch (OrderNotFoundException e) {
			attributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;
		}
	}
	
	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
		try {
			Order order = orderService.getOrder(id);
			List<Country> listCountries = orderService.listAllCountries();
			model.addAttribute("listCountries", listCountries);
			model.addAttribute("order", order);
			model.addAttribute("pageTitle", "Edit Order (ID:" + id + ")");
			return "orders/order_form";
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
			// put key và value vào trong request để gửi qua view(order.html,...)
			request.setAttribute(setting.getKey(), setting.getValue());
			System.out.println(setting.getKey() + ", " + setting.getValue());
		}
	}
	
	private void updateProductDetails(Order order, HttpServletRequest request) {
		String[] detailIds = request.getParameterValues("detailId");
		String[] productIds = request.getParameterValues("productId");
		String[] productPrices = request.getParameterValues("productPrice");
		String[] productDetailCosts = request.getParameterValues("productDetailCost");
		String[] quantities = request.getParameterValues("quantity");
		String[] productSubtotals = request.getParameterValues("productSubtotal");
		String[] productShipCosts = request.getParameterValues("productShipCost");
		
		Set<OrderDetail> orderDetails = order.getOrderDetails();
		for(int i = 0; i < detailIds.length; i++) {
			OrderDetail orderDetail = new OrderDetail();
			Integer detailId = Integer.parseInt(detailIds[i]);
			if (detailId > 0) {
				orderDetail.setId(detailId);
			}
			orderDetail.setOrder(order);
			orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
			orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
			orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
			orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));
			orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));
			orderDetail.setQuantity(Integer.parseInt(quantities[i]));
			
			orderDetails.add(orderDetail);
		}
	}
	
	private void updateOrderTracks(Order order, HttpServletRequest request) {
		String[] trackIds = request.getParameterValues("trackId");
		String[] trackDates = request.getParameterValues("trackDate");
		String[] trackStatus = request.getParameterValues("trackStatus");
		String[] trackNotes = request.getParameterValues("trackNotes");

		List<OrderTrack> orderTracks = order.getOrderTracks();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		for (int i = 0; i < trackIds.length; i++) {
			OrderTrack orderTrack = new OrderTrack();	
			Integer trackId = Integer.parseInt(trackIds[i]);
			if (trackId > 0) {
				orderTrack.setId(trackId);
			}
			orderTrack.setOrder(order);
			orderTrack.setOrderStatus(OrderStatus.valueOf(trackStatus[i]));
			orderTrack.setNotes(trackNotes[i]);
			try {
				orderTrack.setUpdatedTime(dateFormat.parse(trackDates[i]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			orderTracks.add(orderTrack);
		}
	}
	
}


