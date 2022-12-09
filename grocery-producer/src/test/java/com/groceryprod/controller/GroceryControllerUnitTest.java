package com.groceryprod.controller;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groceryprod.event.GroceryEvent;
import com.groceryprod.eventproducer.GroceryEventProducer;
import com.groceryprod.model.Item;

/*
 * 1. @WebMvcTest This annotation is required, if we are writing unit test for controller layer
 * 2. MockMvc - will have access to all method that is part of the specified controller(GroceryController)
 * */

@WebMvcTest(GroceryController.class)
@AutoConfigureMockMvc
public class GroceryControllerUnitTest {

	@Autowired 
	MockMvc mockMvc;
	
	@MockBean
	GroceryEventProducer groceryEventProducer;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@Test
	public void produceGroceryEvent() throws Exception {
		/* given */
		Item item = Item.builder()
					.itemId(43)
					.itemName("unit test")
					.itemPrice(new BigDecimal(434.00))
					.build();
		GroceryEvent groceryEvent = GroceryEvent.builder()
									.eventId(null)
									.item(item)
									.build();
		String json = objectMapper.writeValueAsString(groceryEvent);
		/* when */
		when(groceryEventProducer.produceGroceryEventSync(isA(GroceryEvent.class))).thenReturn(null);
		mockMvc.perform(post("/v1.0/grocery/event")
			   .content(json)
			   .contentType(MediaType.APPLICATION_JSON))
			   .andExpect(status().isCreated());
		/* then */
	}
	
	
	@Test
	public void postGroceryEvent_4XXX() throws Exception {
		/* given */
		Item item = Item.builder()
					.itemId(null)
					.itemName("unit test")
					.itemPrice(new BigDecimal(434.00))
					.build();
		GroceryEvent groceryEvent = GroceryEvent.builder()
									.eventId(null)
									.item(item)
									.build();
		String json = objectMapper.writeValueAsString(groceryEvent);
		/* when */
		when(groceryEventProducer.produceGroceryEventSync(isA(GroceryEvent.class))).thenReturn(null);
		String expectedErrorMsg = "item.itemId_must not be null";
		mockMvc.perform(post("/v1.0/grocery/event")
			   .content(json)
			   .contentType(MediaType.APPLICATION_JSON))
			   .andExpect(status().is4xxClientError())
			   .andExpect(content().string(expectedErrorMsg));
		
	}
	
	
}
