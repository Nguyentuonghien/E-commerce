package com.shopme.admin.setting.country;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryRepositoryTests {

	@Autowired
	private CountryRepository countryRepository;
	
	@Test
	public void testCreateCountry() {
		Country country = new Country("Germany", "GM");
		Country savedCountry = countryRepository.save(country);
		assertThat(savedCountry).isNotNull();
		assertThat(savedCountry.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListCountries() {
		List<Country> countries = countryRepository.findAllByOrderByNameAsc();
		countries.forEach(System.out::println);
		assertThat(countries.size()).isGreaterThan(0);
	}
	
	@Test
	public void testUpdateCountry() {
		Integer id = 1;
		Country country = countryRepository.findById(id).get();
		country.setName("China");
		Country updatedCountry = countryRepository.save(country);
		assertThat(updatedCountry.getName()).isEqualTo("China");
	}
	
	@Test
	public void testGetCountry() {
		Integer id = 3;
		Country country = countryRepository.findById(id).get();
		assertThat(country).isNotNull();
	}
	
	@Test
	public void deleteCountry() {
		countryRepository.deleteById(3);
	}
	
}


