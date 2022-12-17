package com.grocery.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.grocery.entity.GroceryEvent;


public interface GroceryService {

	public void saveOrUpdateGroceryEvent(ConsumerRecord<Integer, String> record) throws JsonMappingException, JsonProcessingException;
	public void save(GroceryEvent event);
	public void update(GroceryEvent event);
}
