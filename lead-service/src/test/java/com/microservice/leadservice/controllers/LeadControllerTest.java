package com.microservice.leadservice.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeAll;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.leadservice.bean.LeadBO;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class LeadControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	
	static LeadBO leadBo;
	
	private final static String URL = "/lead";
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
			}

	@Test
	void testCreateLead() throws JsonProcessingException, Exception {
	leadBo = new LeadBO();
	leadBo.setEmployeeName("praveen");
	leadBo.setProductName("watch");
	leadBo.setDelete(false);
	leadBo.setLeadSourse("working");
	leadBo.setStatus("finished");
	leadBo.setNoOfEmployee(8);
	
		ResultActions respons = mockMvc.perform(post(URL +"/create-lead")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(leadBo)));
		respons.andDo(print()).andExpect(status().isOk());
				}

  @Test
	void testGetAllLeads() throws JsonProcessingException, Exception {
		ResultActions response = mockMvc.perform(get(URL + "/get-all-leads")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(leadBo)));

		response.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(greaterThan(0))));

	}

	@Test
	void testUpdateLead() throws JsonProcessingException, Exception {
	leadBo = new LeadBO();
	leadBo.setEmployeeName("p");
	leadBo.setProductName("watch");
	leadBo.setDelete(false);
	leadBo.setLeadSourse("working");
	leadBo.setStatus("finished");
	leadBo.setNoOfEmployee(8);
	
		ResultActions respons = mockMvc.perform(put(URL +"/update-lead/{id}",33L)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(leadBo)));
		respons.andDo(print()).andExpect(status().isOk());
				
	}


	@Test
	void testGetLeadById() throws JsonProcessingException, Exception {
		leadBo = new LeadBO();
		leadBo.setEmployeeName("manoj");
		leadBo.setProductName("sheets");
	//	leadBo.setDelete(false);
		leadBo.setLeadSourse("new");
		leadBo.setStatus("Finished");
		leadBo.setNoOfEmployee(4);
		ResultActions response = mockMvc.perform(get(URL + "/get-leadById/{id}", 3L)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(leadBo)));

		response.andDo(print()).andExpect(status().isOk());
			

	}
	

	@Test
	void testDeleteLead() throws JsonProcessingException, Exception {
	ResultActions response = mockMvc.perform(delete(URL + "/delete-lead/{id}", 34L)
	.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(leadBo)));

response.andDo(print()).andExpect(status().isOk());

}
	




}
