package com.shopme.admin.order;

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

@Service
public class OrderService {
	
	private static final int ORDERS_PER_PAGE = 10;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CountryRepository countryRepository;
	
	// vì ta phải sortField cho destination 3 lần -> phải custom lại hàm listByPage()
	public void listByPage(int pageNumber, PagingAndSortingHelper helper) {
		String sortField = helper.getSortField();
		String sortDir = helper.getSortDir();
		String keyword = helper.getKeyword();
		Sort sort = null;
		
		// nếu sortField là destination chứa country,city,state -> sort lần lượt theo thứ tự country->state->city
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
	
	public void save(Order orderInform) {
		Order orderInDB = orderRepository.findById(orderInform.getId()).get();
		// orderTime và customer k xuất hiện trong form html -> phải set từ DB cho orderForm
		orderInform.setOrderTime(orderInDB.getOrderTime());
		orderInform.setCustomer(orderInDB.getCustomer());
		orderRepository.save(orderInform);
	}
	
	public Order getOrder(Integer id) throws OrderNotFoundException {
		try {
			return orderRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new OrderNotFoundException("Could not find any orders with ID: " + id);
		}
	}
	
	public void deleteOrder(Integer id) throws OrderNotFoundException {
		Long countById = orderRepository.countById(id);
		if (countById == null || countById == 0) {
			throw new OrderNotFoundException("Could not find any orders with id: " + id);
		}
		orderRepository.deleteById(id);
	}
	
	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc(); 
	}
	
}



