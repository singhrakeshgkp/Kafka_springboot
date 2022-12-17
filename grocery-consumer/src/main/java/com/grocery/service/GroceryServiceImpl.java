package com.grocery.service;

import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.entity.GroceryEvent;
import com.grocery.repo.GroceryEventRepo;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroceryServiceImpl implements GroceryService{

	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired 
	GroceryEventRepo groceryEventRepo;
	
	@Override
	public void saveOrUpdateGroceryEvent(ConsumerRecord<Integer, String> record) throws JsonMappingException, JsonProcessingException {
		
		GroceryEvent event = objectMapper.readValue(record.value(), GroceryEvent.class);
		switch(event.getAction()) {
		case create: 
			save(event);
			break;
		case update:
			update(event);
			break;
		default:
			log.error("not a valid user action ");
			
		}
	}

	@Override
	public void save(GroceryEvent event) {
		event.getItem().setGroceryEvent(event);
		groceryEventRepo.save(event);
		log.info("record saved successfully : {}",event);
	}

	@Override
	public void update(GroceryEvent event) {
		
		if(event.getEventId() == null) {
			throw new IllegalArgumentException("event id can not be null");
		}
		
	  if(event.getEventId().equals(434)) {
		  throw new RecoverableDataAccessException("event id 434 explicitly throwing exp for testing purpose");
	  }
	  if(!groceryEventRepo.existsById(event.getEventId())) {
		  throw new EntityNotFoundException("Record with id "+event.getEventId()+" not found{}"); 
	  }
	  var groceryEventOptional =groceryEventRepo.findById(event.getEventId());
	  
	  event.getItem().setItemId(groceryEventOptional.get().getItem().getItemId());
	  event.getItem().setGroceryEvent(event);
	  groceryEventRepo.save(event);
	}

	

}
