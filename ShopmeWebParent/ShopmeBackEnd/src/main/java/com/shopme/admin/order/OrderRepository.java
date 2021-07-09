package com.shopme.admin.order;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Order;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Integer> {

}
