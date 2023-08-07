package com.groceryprod.eventproducer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groceryprod.event.GroceryEvent;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class GroceryEventProducer {

	@Autowired
	private KafkaTemplate<Integer, String> kafkaTemplate;
	
	@Autowired
	private ObjectMapper mapper;
	
	private static final String TOPIC_NAME = "grocery-event";
	
	/*Approach 1*/
	public void produceGroceryEvent(GroceryEvent groceryEvent) throws JsonProcessingException {
		String message = mapper.writeValueAsString(groceryEvent);
	   kafkaTemplate.sendDefault(message)
	    			.thenAccept((result)->{
	    				 log.info("Message produced on parition {}", result.getRecordMetadata().partition());
	    			
	    			})
	    			.exceptionally(e->{
	    				log.info("Exception Occurred {}",e.getMessage());
	    				return null;
	    			}
	    					)
	    			.thenRun(()->{
	    				log.info("Method executed successfulluy{}");
	    				
	    			});
	 
	}
	
	/*Approach -2 producing record synchronously*/
	public SendResult<Integer, String> produceGroceryEventSync(GroceryEvent groceryEvent) throws InterruptedException, ExecutionException, JsonProcessingException{
		String message = mapper.writeValueAsString(groceryEvent);
		  SendResult<Integer, String> sendResult = kafkaTemplate.sendDefault(message).get();
		return sendResult;
	}
	
 /* Approach -3 and 4 -Producing record on specific Topic, before this we 
  * the only different between approach 3 and 4 is that,in approach 3 we are using null header however in approach 4 it is not
  * were using default topic from appication.prop file */
	
	public CompletableFuture<SendResult<Integer, String>>  produceGroceryEventToSpecifiedTopic(GroceryEvent groceryEvent) throws JsonProcessingException {
		log.info("GroceryEventProducer.produceGroceryEventToSpecifiedTopic() start");
	   Integer key = groceryEvent.getEventId();
	   String value = mapper.writeValueAsString(groceryEvent);
	   ProducerRecord<Integer, String> producerRecord = getProducerRecord(TOPIC_NAME,key,value);
	   CompletableFuture<SendResult<Integer, String>> future= kafkaTemplate.send(producerRecord);
	   
	   future.whenComplete((result,ex)->{
		   if(ex == null) {
			   handleSuccess(result);
		   }else {
			   handleFailureError(ex);
		   }
		});
	   	
	   log.info("GroceryEventProducer.produceGroceryEventToSpecifiedTopic() end");
	   return future;
	}
	
	

	private ProducerRecord<Integer, String> getProducerRecord(String topicName, Integer key, String value) {
		/*
		 * approach -4 start
		 */
		List<Header> headers = List.of(new RecordHeader("event-source", "scanner".getBytes()));

		/*
		 * approach -4 end
		 */

		return new ProducerRecord<Integer, String>(topicName, null, key, value, headers);
	}

	public static void handleSuccess(SendResult<Integer, String> sendResult) {

		 log.info("Message produced on parition {}", sendResult.getRecordMetadata().partition());
		 Stream.iterate(0, n->n+1)
		 	   .limit(3)
		 	   .forEach(n->{
		 		   log.info("counting i {}",n);
		 		   sleep(1000);
		 	   });
	   
		
	}
	public static void handleFailureError(Throwable ex) {
		log.error("error occurred while producing record {}",ex.getMessage());
	}
	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
