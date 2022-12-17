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
public class GroceryEventRetryConsumer {

	 @Autowired
	   GroceryService groceryService;
	   
		/*
		 * set autostartup to 'false' while testing your application form unit test or
		 * integration test. Since its again calling groceryService.saveOrUpdate it will go round and round(infinite loop)
		 */
	 @KafkaListener(topics = {"${topics.retry}"}, autoStartup = "${retrylistener.startup:false}")
		public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonMappingException, JsonProcessingException {
			
		 log.info("GroceryEventRetryConsumer.onMessage() start");
			consumerRecord.headers().forEach(header->{
				log.info("key {} and value {}",header.key(),new String(header.value()));
			});
			
			/*can perform database operation here if*/
			//groceryService.saveOrUpdateGroceryEvent(consumerRecord);
			log.info("saved record is {} ",consumerRecord);
			log.info("GroceryEventRetryConsumer.onMessage() end");
		}
}
