package com.shopme.admin.setting.state;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import com.shopme.common.entity.StateDTO;


@RestController
public class StateRestController {
	
	@Autowired
	private StateRepository stateRepository;
	
	@GetMapping("/states/list_by_country/{id}")
	public List<StateDTO> listByCountry(@PathVariable("id") Integer countryId) {
		List<State> listStates = stateRepository.findByCountryOrderByNameAsc(new Country(countryId));
		List<StateDTO> result = new ArrayList<>();
		listStates.forEach(state -> {
			result.add(new StateDTO(state.getId(), state.getName()));
		});
		return result;
	}
	
	/**
	 * @RequestBody -> Spring sẽ convert JSON data được gửi từ client thành 1 Java Object là State
	 */
	@PostMapping("/states/save")
	public String save(@RequestBody State state) {
		stateRepository.save(state);
		return String.valueOf(state.getId());
	}
	
	@DeleteMapping("/states/delete/{id}")
	public void delete(@PathVariable("id") Integer id) {
		stateRepository.deleteById(id);
	}
	
}



