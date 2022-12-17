package com.grocery.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.grocery.service.GroceryService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GroceryEventConsumer {

	/*1. Here we are going to get the record in ConsumerRecord obj, while producing we passed producer record
	 *2. can pass multiple topic name here
	 * */
	
   @Autowired
   GroceryService groceryService;
   
	@KafkaListener(topics = {"grocery-event"}, groupId = "${spring.kafka.consumer.group-id}")
	public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonMappingException, JsonProcessingException {
		groceryService.saveOrUpdateGroceryEvent(consumerRecord);
		log.info("saved record is {} ",consumerRecord);
	}
}
