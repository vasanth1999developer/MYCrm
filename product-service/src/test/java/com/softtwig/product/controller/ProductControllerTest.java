package com.softtwig.product.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.microservice.product.bo.ProductBo;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private ObjectMapper objectMapper;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	private final static String URL = "/product";

	@Test
	//@Disabled
	void testCreateProduct() throws JsonProcessingException, Exception {
		ProductBo productBo = new ProductBo();
		productBo.setProductName("MB-Whey Protein");
		productBo.setMinStocks(3L);
		productBo.setMaxStocks(5l);
		productBo.setAvaliablesStocks(10l);
		productBo.setProductSpecification("MuscleBlaze® Raw Whey Protein is a muscle-making");
		productBo.setProductType("Protein");
		
		ResultActions respons = mockMvc.perform(post(URL + "/create-product")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productBo)));
		respons.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.productName", is(productBo.getProductName())))
				.andExpect(jsonPath("$.productSpecification", is(productBo.getProductSpecification())));
	}
	

	@Test
	void testGetAllProduct() throws JsonProcessingException, Exception {
	ProductBo productBo = new ProductBo();
		ResultActions response = mockMvc.perform(get(URL + "/get-all-product")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productBo)));

		response.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.size()", greaterThan(0)));

	}

	


	@Test
	//@Disabled
	void testUpdateProduct() throws Exception {
		ProductBo productBo = new ProductBo();
		productBo.setProductName("MUSCLEBLAZE-Whey Protein");
		productBo.setMinStocks(3L);
		productBo.setMaxStocks(5l);
		productBo.setAvaliablesStocks(10l);
		productBo.setProductSpecification("MuscleBlaze® Raw Whey Protein is a muscle-making");
		productBo.setProductType("Protein");

		ResultActions response = mockMvc.perform(put(URL + "/update-product/{id}", 27L)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productBo)));

		response.andDo(print()).andExpect(status().isOk())
		.andExpect(jsonPath("$.productName", is(productBo.getProductName())))
		.andExpect(jsonPath("$.productSpecification", is(productBo.getProductSpecification())));
	}

	@Test
	@Disabled
	void testDeleteProduct() throws JsonProcessingException, Exception {
		ProductBo productBo = new ProductBo();
		ResultActions response = mockMvc.perform(delete(URL + "/delete-product/{id}", 28L)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productBo)));

		response.andDo(print()).andExpect(status().isOk());
				
	}

	@Test
	void testGetSingleProduct() throws JsonProcessingException, Exception {
		ProductBo productBo = new ProductBo();
		productBo.setProductName("MUSCLEBLAZE-Whey Protein");
		productBo.setMinStocks(3L);
		productBo.setMaxStocks(5l);
		productBo.setAvaliablesStocks(10l);
		productBo.setProductSpecification("MuscleBlaze® Raw Whey Protein is a muscle-making");
		productBo.setProductType("Protein");

		ResultActions response = mockMvc.perform(get(URL + "/get-single-id/{productId}", 27L)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(productBo)));

		response.andDo(print()).andExpect(status().isOk())
		.andExpect(jsonPath("$.productName", is(productBo.getProductName())))
		.andExpect(jsonPath("$.productSpecification", is(productBo.getProductSpecification())));
	}


}