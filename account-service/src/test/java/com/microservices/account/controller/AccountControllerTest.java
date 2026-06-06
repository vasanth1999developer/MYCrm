package com.microservices.account.controller;

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

import org.springframework.http.MediaType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.account.model.AccountBO;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AccountControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	
	AccountBO bo;
	
	private final static String URL = "/account";
	
	

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		bo=new AccountBO();
		bo.setAccountName("Manoj");
		bo.setAccountOwner("mano");
		bo.setAnnualRevenue("100000");
		bo.setCity("trichy");
		bo.setContactNumber(8220580445l);
		bo.setCountry("India");
		bo.setDescription("Completed tranction");
		bo.setEmail("manoj123@gmail.com");
		bo.setIndustry("IT");
		bo.setIsdelete(false);
		bo.setNoOfEmploye(22);
		bo.setParentAccount("Ravi");
		bo.setPincode(621012l);
		bo.setSalutation("Mr");
		bo.setState("Tamil Nadu");
		bo.setStreet("South Street");
		
		
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
//	@Disabled

	void testCreateAccount() throws Exception {
		ResultActions respons = mockMvc.perform(post(URL + "/create-account")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(bo)));
		respons.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.accountOwner", is(bo.getAccountOwner())))
				.andExpect(jsonPath("$.accountName", is(bo.getAccountName())));
	}

	@Test
//	@Disabled
	void testGetAllAccount() throws Exception {
		ResultActions response = mockMvc.perform(get(URL + "/getAllAccount")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(bo)));

		response.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.size()", greaterThan(0)));
		
	}

	@Test
//	@Disabled
	void testUpdateAccount() throws JsonProcessingException, Exception {
		bo.setEmail("mani@gmai.com");
		bo.setAccountName("Manoj");
		bo.setAccountOwner("mano");
		bo.setAnnualRevenue("100000");
		bo.setCity("trichy");
		bo.setContactNumber(8220580445l);
		bo.setCountry("India");
		bo.setDescription("Completed tranction");
		
		bo.setIndustry("IT");
		bo.setIsdelete(false);
		bo.setNoOfEmploye(22);
		bo.setParentAccount("Ravi");
		bo.setPincode(621012l);
		bo.setSalutation("Mr");
		bo.setState("Tamil Nadu");
		bo.setStreet("South Street");
		ResultActions response = mockMvc.perform(put(URL + "/update-account/{id}", 4)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(bo)));
		
		response.andDo(print()).andExpect(status().isOk())
		.andExpect(jsonPath("$.email", is(bo.getEmail())));
	}

	@Test
//	@Disabled
	void testGetAccountById() throws JsonProcessingException, Exception {
		
		ResultActions response = mockMvc.perform(get(URL + "/get-account/{id}", 4)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(bo)));
		
		response.andDo(print()).andExpect(status().isOk())
		.andExpect(jsonPath("$.accountOwner", is(bo.getAccountOwner())))
		.andExpect(jsonPath("$.accountName", is(bo.getAccountName())));
	}

	@Test
////	@Disabled
	void testDeletAccountById() throws JsonProcessingException, Exception {
		bo.setIsdelete(true);
		ResultActions response = mockMvc.perform(delete(URL + "/delete-account/{id}", 4)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(bo)));
		response.andDo(print()).andExpect(status().isOk());
		
		
	}

}
