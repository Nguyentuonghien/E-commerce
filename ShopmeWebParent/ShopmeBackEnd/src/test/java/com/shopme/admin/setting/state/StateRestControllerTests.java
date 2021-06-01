package com.shopme.admin.setting.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTests {
	
	@Autowired
	private StateRepository stateRepository;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	@WithMockUser(username = "nam@codejava.net", password = "minh2020", roles = "ADMIN")
	public void testListByCountry() throws Exception {
		Integer countryId = 2;
		String url = "/states/list_by_country/" + countryId;
		MvcResult result = mockMvc.perform(get(url))
				             .andExpect(status().isOk())
				             .andDo(print())
				             .andReturn();
		// read json data từ response body
		String jsonResponse = result.getResponse().getContentAsString();
		// covert json to java object
		State[] states = objectMapper.readValue(jsonResponse, State[].class);
		assertThat(states).hasSizeGreaterThan(1);
	}
	
	@Test
	@WithMockUser(username = "nam@codejava.net", password = "minh2020", roles = "ADMIN")
	public void testCreateState( ) throws Exception {
		Integer countryId = 2;
		String url = "/states/save";
		Country country = countryRepository.findById(countryId).get();
		State state = new State("Arizona", country);
		MvcResult result = mockMvc.perform(post(url).contentType("application/json")
				                  .content(objectMapper.writeValueAsString(state))
				                  .with(csrf()))
		       .andExpect(status().isOk())
		       .andDo(print())
		       .andReturn();
		// vì save() trả về id dạng chuỗi -> convert thành Integer
		String response = result.getResponse().getContentAsString();
		Integer stateId = Integer.parseInt(response);
		Optional<State> findById = stateRepository.findById(stateId);
		
		assertThat(findById.isPresent());
	}
	
	@Test
	@WithMockUser(username = "nam@codejava.net", password = "minh2020", roles = "ADMIN")
	public void testUpdateState( ) throws Exception {
		Integer stateId = 9;
		String url = "/states/save";
		State state = stateRepository.findById(stateId).get();
		state.setName("Alaska");
		mockMvc.perform(post(url).contentType("application/json")
				                 .content(objectMapper.writeValueAsString(state))
				                 .with(csrf()))
		       .andExpect(status().isOk())
		       .andExpect(content().string(String.valueOf(stateId)))
		       .andDo(print());
		// vì save() trả về id dạng chuỗi -> convert thành Integer
		Optional<State> findById = stateRepository.findById(stateId);
		assertThat(findById.isPresent());
		
		State updatedState = findById.get();
		assertThat(updatedState.getName()).isEqualTo("Alaska");
	}
	
	@Test
	@WithMockUser(username = "nam@codejava.net", password = "minh2020", roles = "ADMIN")
	public void testDeleteState() throws Exception {
		Integer stateId = 9;
		String url = "/states/delete/" + stateId;
		mockMvc.perform(get(url)).andExpect(status().isOk());
		
		Optional<State> findById = stateRepository.findById(stateId);
		assertThat(findById).isNotPresent();
	}
	
}



