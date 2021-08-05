package com.shopme.setting;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Currency;

@Repository
public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
	
}
