package com.groceryprod.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.groceryprod.event.GroceryEvent;
import com.groceryprod.eventproducer.GroceryEventProducer;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class GroceryController {

	@Autowired
	private GroceryEventProducer groceryEventProducer;
	
	@PostMapping("/v1.0/grocery/event")
	public ResponseEntity<GroceryEvent> produceGroceryEvent(@RequestBody GroceryEvent groceryEvent) throws JsonProcessingException, InterruptedException, ExecutionException{
		log.info("GroceryController.produceGroceryEvent() start");
		/*
		 *1-
		 */
		// groceryEventProducer.produceGroceryEvent(groceryEvent);
		/*2.
		 * */ 
		
		  SendResult<Integer, String> result
		  =groceryEventProducer.produceGroceryEventSync(groceryEvent);
		  //log.info("produced event is {}",result.getProducerRecord());
		 
		/*3-*/
		/* groceryEventProducer.produceGroceryEventToSpecifiedTopic(groceryEvent); */
	 log.info("GroceryController.produceGroceryEvent() End");
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
}
