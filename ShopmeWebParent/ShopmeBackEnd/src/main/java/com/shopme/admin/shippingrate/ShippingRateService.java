package com.shopme.admin.shippingrate;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.product.ProductRepository;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.product.Product;

@Service
@Transactional
public class ShippingRateService {
	
	public static final int SHIPPINGRATE_PER_PAGE = 10;
	
	public static final int DIM_DIVISOR = 139;
	
	@Autowired private ShippingRateRepository shippingRateRepository;
	
	@Autowired private CountryRepository countryRepository;
	
	@Autowired private ProductRepository productRepository;
	
	public void listByPage(int pageNumber, PagingAndSortingHelper helper) {
		helper.listEntities(pageNumber, SHIPPINGRATE_PER_PAGE, shippingRateRepository);
	}
	
	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	public void save(ShippingRate shippingRateInForm) throws ShippingRateAlreadyExistsException {
		ShippingRate shippingRateInDB = shippingRateRepository.findByCountryAndState(shippingRateInForm.getCountry().getId(), shippingRateInForm.getState());
		// tìm shippingRate trong DB theo country&state, nếu tìm thấy và trong chế độ add(id==null) 
		// -> báo lỗi đã tồn tại shippingRate với country&state tương tự trong DB
		boolean foundExistingRateInNewMode = shippingRateInDB != null && shippingRateInForm.getId() == null;
		// tương tự nếu tìm thấy và trong chế độ edit(id != null) và 2 object ShippingRate trong DB và form lại có id khác nhau(dùng equal())
		// -> báo lỗi đã tồn tại shippingRate với country&state tương tự trong DB
		boolean foundDifferentExistInEditMode = shippingRateInDB != null && shippingRateInForm.getId() != null 
				                                && !shippingRateInDB.equals(shippingRateInForm);
		
		if (foundExistingRateInNewMode || foundDifferentExistInEditMode) {
			throw new ShippingRateAlreadyExistsException("There is already a rate for the destination " + shippingRateInForm.getCountry().getName()
					              + ", " + shippingRateInForm.getState());
		}
		shippingRateRepository.save(shippingRateInForm);
	}
	
	public ShippingRate getShippingRate(Integer id) throws ShippingRateNotFoundException {
		try {
		    return shippingRateRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ShippingRateNotFoundException("Could not find any shipping rate with ID: " + id);
		}    
	}
	
	public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
		Long countById = shippingRateRepository.countById(id);
		if (countById == null || countById == 0) {
			throw new ShippingRateNotFoundException("Could not find any shipping rate with ID: " + id);
		}
		shippingRateRepository.updateCODSupport(id, codSupported);
	}
	
	public void delete(Integer id) throws ShippingRateNotFoundException {
		Long countById = shippingRateRepository.countById(id);
		if (countById == null || countById == 0) {
			throw new ShippingRateNotFoundException("Could not find any shipping rate with ID: " + id);
		}
		shippingRateRepository.deleteById(id);
	}
	
	public float calculateShippingCost(Integer productId, Integer countryId, String state) throws ShippingRateNotFoundException {
		ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(countryId, state);
		if (shippingRate == null) {
			throw new ShippingRateNotFoundException("No shipping rate found for the given destination. You have to enter shipping cost manually.");
		}
		Product product = productRepository.findById(productId).get();
		float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
		float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
		return finalWeight * shippingRate.getRate();
	}
	
}




