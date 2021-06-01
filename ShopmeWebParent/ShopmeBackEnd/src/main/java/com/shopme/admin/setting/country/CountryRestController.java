package com.shopme.admin.setting.country;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Country;

@RestController
public class CountryRestController {
	
	@Autowired
	private CountryRepository countryRepository;
	
	@GetMapping("/countries/list")
	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	/**
	 * Vì ta xây dựng API, nên các thông tin từ phía Client gửi lên Server sẽ nằm trong Body dưới dạng JSON. Spring Boot sẽ giúp ta
	 * chuyển chuỗi JSON trong request thành một Object Java, chỉ cần cho nó biết cần chuyển JSON thành Object nào bằng Annotation @RequestBody
	 *
	 */
	@PostMapping("/countries/save")
	public String saveCountry(@RequestBody Country country) {
		// chuyển chuỗi json trong request thành object country để lưu vào DB
		Country savedCountry = countryRepository.save(country);
		// trả về id của object country dưới dạng String
		return String.valueOf(savedCountry.getId());
	}
	
	@DeleteMapping("/countries/delete/{id}")
	public void deleteCountry(@PathVariable("id") Integer id) {
		countryRepository.deleteById(id);
	}
	
}



