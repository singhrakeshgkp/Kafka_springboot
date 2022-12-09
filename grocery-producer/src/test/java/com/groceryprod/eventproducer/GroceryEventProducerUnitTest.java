package com.groceryprod.eventproducer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groceryprod.constants.TestConstants;
import com.groceryprod.event.GroceryEvent;
import com.groceryprod.model.Item;

@ExtendWith(MockitoExtension.class)
public class GroceryEventProducerUnitTest {

	@Mock
	KafkaTemplate<Integer, String> kafkaTemplate;
	
	/*Here we are not mocking toString conversion behaviour
	 * we want this conversion happens by object mapper*/
	@Spy
	ObjectMapper objectMapper = new ObjectMapper();
	
	@InjectMocks
	GroceryEventProducer groceryEventProducer;
	
	
	
	@Test
	void produceGroceryEventToSpecifiedTopic_failScenario() throws JsonProcessingException {
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
		CompletableFuture<SendResult<Integer, String>> future = new CompletableFuture<>();
		future.obtrudeException(new RuntimeException("exception calling fafka"));
		
		/* when */
		when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
		assertThrows(Exception.class, ()-> groceryEventProducer.produceGroceryEventToSpecifiedTopic(groceryEvent).get());
	}
	
	@Test
	void produceGroceryEventToSpecifiedTopic_success() throws JsonProcessingException, InterruptedException, ExecutionException {
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
		CompletableFuture<SendResult<Integer, String>> future = new CompletableFuture<>();
		ProducerRecord<Integer, String> producerRecord = new ProducerRecord<Integer, String>(TestConstants.GROCERY_EVENT, groceryEvent.getEventId(), json);
		/*
		 * In metadata provided random value to partition baseOffset, batchIndex, serialized key and value
		 * */
		RecordMetadata recordMetaData = new RecordMetadata(new TopicPartition(TestConstants.GROCERY_EVENT, 2), 1, 1, System.currentTimeMillis(), 1, 2);
		SendResult<Integer, String> sendResult = new SendResult<>(producerRecord, recordMetaData);
		future.obtrudeValue(sendResult);
		/* when */
		
		when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
		CompletableFuture<SendResult<Integer, String>> returnedFutureObj = groceryEventProducer.produceGroceryEventToSpecifiedTopic(groceryEvent);
		
		/* then */
		SendResult<Integer, String> result = returnedFutureObj.get();
		assert result.getRecordMetadata().partition() == 2;
		
	}
}
