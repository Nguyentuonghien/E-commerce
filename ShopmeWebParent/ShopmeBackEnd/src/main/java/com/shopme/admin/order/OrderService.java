package com.shopme.admin.order;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.OrderTrack;

@Service
public class OrderService {
	
	public static final int ORDERS_PER_PAGE = 10;
	
	@Autowired private OrderRepository orderRepository;
	
	@Autowired private CountryRepository countryRepository;
	
	public void listByPage(int pageNumber, PagingAndSortingHelper helper) {
		String sortField = helper.getSortField();
		String sortDir = helper.getSortDir();
		String keyword = helper.getKeyword();
		Sort sort = null;
		// nếu sortField là trường destination(chứa country,city,state) -> sort lần lượt theo thứ tự country->state->city
		if (sortField.equals("destination")) {
			sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
		} else {
			sort = Sort.by(sortField);
		}
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNumber - 1, ORDERS_PER_PAGE, sort);
		Page<Order> page = null;
		if (keyword != null) {
			page = orderRepository.findAll(keyword, pageable);
		} else {
			page = orderRepository.findAll(pageable);
		}
		helper.updateModelAttributes(pageNumber, page);
	}
	
	public void save(Order orderInForm) {
		Order orderInDB = orderRepository.findById(orderInForm.getId()).get();
		// orderTime và customer k xuất hiện trong form html -> phải set từ DB cho orderForm
		orderInForm.setOrderTime(orderInDB.getOrderTime());
		orderInForm.setCustomer(orderInDB.getCustomer());
		orderRepository.save(orderInForm);
	}
	
	public Order getOrder(Integer id) throws OrderNotFoundException {
		try {
		    return orderRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new OrderNotFoundException("Could not find any orders with ID: " + id);
		}    
	}
	
	public void updateStatus(Integer orderId, String status) {
		Order orderInDB = orderRepository.findById(orderId).get();
		// convert status từ string thành enum sau đó check trong list OrderStatus của Order xem
		// đã có status truyền vào chưa, nếu chưa -> set status đó cho Order và OrderTrack
		OrderStatus statusToUpdate = OrderStatus.valueOf(status);
		if (!orderInDB.hasStatus(statusToUpdate)) {
			List<OrderTrack> listOrderTracks = orderInDB.getOrderTracks();
			OrderTrack orderTrack = new OrderTrack();
			orderTrack.setOrder(orderInDB);
			orderTrack.setOrderStatus(statusToUpdate);
			orderTrack.setUpdatedTime(new Date());
			orderTrack.setNotes(statusToUpdate.defaultDescription());
			listOrderTracks.add(orderTrack);
			
			orderInDB.setStatus(statusToUpdate);
			orderRepository.save(orderInDB);
		}
	}
	
	public void deleteOrder(Integer id) throws OrderNotFoundException {
		Long countById = orderRepository.countById(id);
		if (countById == null || countById == 0) {
			throw new OrderNotFoundException("Could not find any orders with ID: " + id);
		}
		orderRepository.deleteById(id);
	}
	
	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}
	
}


