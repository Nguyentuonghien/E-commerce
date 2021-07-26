package com.shopme.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.shopme.common.entity.order.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
	
	
	
}
