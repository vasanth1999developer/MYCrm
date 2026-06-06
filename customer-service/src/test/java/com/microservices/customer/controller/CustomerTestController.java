package com.microservices.customer.controller;

import static org.junit.jupiter.api.Assertions.*;

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
import com.microservices.customer.model.CustomerBo;

import org.junit.jupiter.api.Test;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CustomerTestController {

	
	
	 

		@Autowired
		private MockMvc mockMvc;
		
		@Autowired
		private ObjectMapper objectMapper;
		
		CustomerBo customerBo;
		
		private final static String URL = "/customer";
		
		HashMap<String, Object> map;
		
		@BeforeEach
		void setUp() throws Exception {
			customerBo = new CustomerBo();
			
			customerBo.setFirstName("Arun");
			customerBo.setLastName("K");
			customerBo.setContactNo("9940367304");;
			customerBo.setMobileNo("7867054220");
			customerBo.setAddress("chennai");
			customerBo.setWebSite("http://www.techymeet.com");
			customerBo.setEmailId("Arun.p@gmail.com");
			customerBo.setProductName("Wood");
			customerBo.setIndustry("nurturing");
			customerBo.setDelete(false);
			 
			
			map = new HashMap<String, Object>();
			
			map.put("CustomerBo", customerBo);
			
			
		}
		
		
		@Test
		@Disabled
		void testCreateCustomer() throws Exception {
			ResultActions respons = mockMvc.perform(post(URL+"/v1/create-customer")
					.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerBo)));
			respons.andDo(print()).andExpect(status().isOk());
		}
		
		
		
		
		@Test
		@Disabled
		void testCustomerProfile() throws Exception {
			ResultActions response = mockMvc.perform(get(URL + "/v1/view-customer")
					.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerBo)));

			response.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(greaterThan(0))));

		}
		
		
		
		@Test
		@Disabled
		void testCustomerDetail() throws Exception {
			CustomerBo customerBo = new CustomerBo();

			customerBo.setFirstName("jayapraksdfsf");
			customerBo.setLastName("K");
			customerBo.setContactNo("9940367304");;
			customerBo.setMobileNo("7867054220");
			customerBo.setAddress("SathishKumar");
			customerBo.setWebSite("http://www.techymeet.com");
			customerBo.setEmailId("jayaprakash.p@gmail.com");
			customerBo.setProductName("Wood");
			customerBo.setIndustry("nurturing");
			customerBo.setDelete(false);

			ResultActions response = mockMvc.perform(get(URL + "/v1/edit-customer/{id}", 2L)
					.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerBo)));

			response.andDo(print()).andExpect(status().isOk());
				

		}

		@Test
		//@Disabled
		void testDeleteCustomer() throws Exception {
			ResultActions response = mockMvc.perform(delete(URL + "/v1/delete-customer/{id}", 3L)
					.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerBo)));

			response.andDo(print()).andExpect(status().isOk());
		}

		@Test
		@Disabled
		void testUpdateCustomer() throws Exception {
			customerBo.setCustomerId(2l);
			customerBo.setFirstName("jayapraksdfsf");
			customerBo.setLastName("K");
			customerBo.setContactNo("9940367304");;
			customerBo.setMobileNo("7867054220");
			customerBo.setAddress("SathishKumar");
			customerBo.setWebSite("http://www.techymeet.com");
			customerBo.setEmailId("jayaprakash.p@gmail.com");
			customerBo.setProductName("Wood");
			customerBo.setIndustry("nurturing");
			customerBo.setDelete(false);

			ResultActions response = mockMvc.perform(put(URL + "/v1/update-customer")
					.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customerBo)));

			response.andDo(print()).andExpect(status().isOk());
				
		}
		
		


	
}
