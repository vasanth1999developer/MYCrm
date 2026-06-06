package com.techymeet.opportunity.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.opportunity.models.entity.OpportunityVo;
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class OpportunityTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	OpportunityVo opportunityVo;
	
	private final static String URL = "/opportunity";
	
	HashMap<String, Object> map;
	
	@BeforeEach
	void setUp() throws Exception {
		opportunityVo = new OpportunityVo();
		
		opportunityVo.setFirstName("Arun");
		opportunityVo.setLastName("K");
		opportunityVo.setProbability(100d);
		opportunityVo.setSalutation("Mr");
		opportunityVo.setSalesStage("completed");
		opportunityVo.setAmount(900d);
		opportunityVo.setEmailAddress("arun@gmail.com");
		opportunityVo.setDescription("good");
		opportunityVo.setDelete(false);
		opportunityVo.setActive(true);
		
		map = new HashMap<String, Object>();
		
		map.put("OpportunityVo", opportunityVo);
		
		
	}
	
	
	@Test
	@Disabled
	void testCreateOpportunity() throws Exception {
		ResultActions respons = mockMvc.perform(post(URL+"/create-opportunity")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(opportunityVo)));
		respons.andDo(print()).andExpect(status().isOk());
	}
	
	
	
	
	@Test
	@Disabled
	void testOpportunityProfile() throws Exception {
		ResultActions response = mockMvc.perform(get(URL + "/list-opportunity")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(opportunityVo)));

		response.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(greaterThan(0))));

	}
	
	
	
	@Test
	@Disabled
	void testOpportunityDetail() throws Exception {
		OpportunityVo opportunityVo = new OpportunityVo();

		opportunityVo.setFirstName("Vanchi");
		opportunityVo.setLastName("A");
		opportunityVo.setSalutation("mr");
		opportunityVo.setAmount(100001d);
		opportunityVo.setSalesStage("completed");
		opportunityVo.setEmailAddress("vanchi@gmail.com");

		ResultActions response = mockMvc.perform(get(URL + "/view-opportunity/{id}", 9L)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(opportunityVo)));

		response.andDo(print()).andExpect(status().isOk());
			

	}

	@Test
	@Disabled
	void testDeleteOpportunity() throws Exception {
		ResultActions response = mockMvc.perform(delete(URL + "/delete-opportunity/{id}", 43L)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(opportunityVo)));

		response.andDo(print()).andExpect(status().isOk());
	}

	@Test
//	@Disabled
	void testUpdateOpportunity() throws Exception {

		opportunityVo.setFirstName("Arun");
		opportunityVo.setLastName("K");
		opportunityVo.setProbability(100d);
		opportunityVo.setSalutation("Mr");
		opportunityVo.setSalesStage("completed");
		opportunityVo.setAmount(900d);
		opportunityVo.setEmailAddress("arun@gmail.com");
		opportunityVo.setDescription("good");
		opportunityVo.setDelete(false);
		opportunityVo.setActive(true);
		opportunityVo.setOpportunityId(43l);

		ResultActions response = mockMvc.perform(put(URL + "/update-opportunity")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(opportunityVo)));

		response.andDo(print()).andExpect(status().isOk());
			
	}
	
	


}
