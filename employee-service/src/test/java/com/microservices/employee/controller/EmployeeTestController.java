package com.microservices.employee.controller;

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
import com.microservices.employee.model.EmployeeBo;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EmployeeTestController {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	EmployeeBo employeeBo;
	
	private final static String URL = "/employee";
	
	HashMap<String, Object> map;
	
	@BeforeEach
	void setUp() throws Exception {
		employeeBo = new EmployeeBo();
		
		employeeBo.setFirstName("vsnchi");
		employeeBo.setLastName("nathan");
		employeeBo.setMobileNo(7373836855l);
		employeeBo.setEmailAddress("vanchi73@gmail.com");
		employeeBo.setPassword("vanchi81223");
		//employeeBo.setConfirmPassword("vanchi81223");
		employeeBo.setAddress("chennai");
		employeeBo.setState("tamilnadu");
		employeeBo.setCountry("india");
		employeeBo.setDelete(false);
		
		map = new HashMap<String, Object>();
		
		map.put("EmployeeBo", employeeBo);
		
		
	}
	
	
	@Test
	@Disabled
	void testCreateEmployee() throws Exception {
		ResultActions respons = mockMvc.perform(post(URL+"/create-employee")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(employeeBo)));
		respons.andDo(print()).andExpect(status().isOk());
	}
	
	
	
	
	@Test
	@Disabled
	void testEmployeeProfile() throws Exception {
		ResultActions response = mockMvc.perform(get(URL + "/get-all-employees")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(employeeBo)));

		response.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.size()", is(greaterThan(0))));

	}
	
	
	
	@Test
	@Disabled
	void testEmployeeDetail() throws Exception {
		EmployeeBo employeeBo = new EmployeeBo();

		employeeBo.setFirstName("vsnchi");
		employeeBo.setLastName("nathan");
		employeeBo.setMobileNo(7373836855l);
		employeeBo.setEmailAddress("vanchi73@gmail.com");
		employeeBo.setPassword("vanchi81223");
		//employeeBo.setConfirmPassword("vanchi81223");
		employeeBo.setAddress("chennai");
		employeeBo.setState("tamilnadu");
		employeeBo.setCountry("india");
		employeeBo.setDelete(false);
		
		ResultActions response = mockMvc.perform(get(URL + "/get-employeeById/{id}", 22L)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(employeeBo)));

		response.andDo(print()).andExpect(status().isOk());
			

	}

	@Test
	//@Disabled
	void testDeleteEmployee() throws Exception {
		ResultActions response = mockMvc.perform(delete(URL + "/delete-employee/{id}", 22L)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(employeeBo)));

		response.andDo(print()).andExpect(status().isOk());
	}

	@Test
	@Disabled
	void testUpdateEmployee() throws Exception {

		employeeBo.setFirstName("v");
		employeeBo.setLastName("nathan");
		employeeBo.setMobileNo(7373836855l);
		employeeBo.setEmailAddress("vanchi73@gmail.com");
		employeeBo.setPassword("vanchi81223");
		//employeeBo.setConfirmPassword("vanchi81223");
		employeeBo.setAddress("chennai");
		employeeBo.setState("tamilnadu");
		employeeBo.setCountry("india");
		employeeBo.setDelete(false);

		ResultActions response = mockMvc.perform(put(URL + "/update-employee")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(employeeBo)));

		response.andDo(print()).andExpect(status().isOk());
			
	}
	
	

}
