package com.grocery.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.consumer.GroceryEventConsumer;
import com.grocery.entity.Action;
import com.grocery.entity.GroceryEvent;
import com.grocery.repo.GroceryEventRepo;
import com.grocery.repo.GroceryFailledRecordLogsRepo;
import com.grocery.service.GroceryService;


@SpringBootTest
@EmbeddedKafka(topics = {"grocery-event","grocery-event-retry","grocery-event.dlt"},partitions = 2)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}","spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}","retrylistener.startup=false"})
public class GroceryConsumerIntegTest {
	
	@Autowired
	EmbeddedKafkaBroker embeddedKafkaBroker;
	
	@Autowired
	KafkaTemplate<Integer, String> kafkaTemplate;
	
	@Autowired
	KafkaListenerEndpointRegistry endpointRegistry;
	
	@SpyBean
	GroceryEventConsumer groceryEventConsumerSpy;
	
	@SpyBean
	GroceryService groceryServiceSpy;
	
	@SpyBean
	GroceryEventRepo groceryEventRepoSpy;
	
	@Spy
	ObjectMapper objMapper = new ObjectMapper();
	
	@Value("${topics.retry}")
	private String retryTopicName;
	
	@Value("${topics.dlt}")
	private String deadLetterTopicName;
	
	private Consumer<Integer, String> consumer;
	
	@Autowired
	private GroceryFailledRecordLogsRepo failledRecordLogsRepo;
	
	@BeforeEach
	void init() {
		/*since we have another listener that is retry listener/consumer
		 * so changing it to wait for assignment to a pearticular group*/
		/*  
		 * for(MessageListenerContainer container :
		 * endpointRegistry.getAllListenerContainers()) {
		 * ContainerTestUtils.waitForAssignment(container,
		 * embeddedKafkaBroker.getPartitionsPerTopic());
		 * 
		 * }
		 */
		var container = endpointRegistry.getAllListenerContainers()
						.stream()
						.filter(listenerContainer-> listenerContainer.getGroupId()
								.equalsIgnoreCase("grocery-event-group"))
						.collect(Collectors.toList()).get(0);
		
		ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
		
	}
	@AfterEach
	void clear() {

		groceryEventRepoSpy.deleteAll();
		failledRecordLogsRepo.deleteAll();
	}
	//@Test
	public void createGroceryEventMsg() throws InterruptedException, ExecutionException, JsonMappingException, JsonProcessingException {
		
		/*given*/
		String json = "{\"action\":\"create\",\"item\": {\"itemName\": \"oats\",\"itemPrice\": 55.439999999}}";
		kafkaTemplate.sendDefault(json).get();
		/*when*/
		CountDownLatch latch = new CountDownLatch(1);
		latch.await(5, TimeUnit.SECONDS);
		/* then */
		verify(groceryEventConsumerSpy,times(1)).onMessage(isA(ConsumerRecord.class));
		verify(groceryServiceSpy,times(1)).saveOrUpdateGroceryEvent(isA(ConsumerRecord.class));
	   List<GroceryEvent> recordList = (List<GroceryEvent>) groceryEventRepoSpy.findAll();
	   assert recordList.size() == 1;
	   recordList.forEach((record)->{
		   assert record.getEventId() !=null;
		   assertEquals("oats", record.getItem().getItemName());
	   });
	   assertEquals(recordList.size(), 1);
	}
	
 //@Test
 void updateGroceryEventMsg() throws InterruptedException, ExecutionException, JsonMappingException, JsonProcessingException {
	
	 /*given*/
	 /*
		 * Step 1. Persist the record in db
	    */
		String jsonStr = "{\"action\":\"create\",\"item\": {\"itemName\": \"oats\",\"itemPrice\": 55.439999999}}";
		
		GroceryEvent groceryObj = objMapper.readValue(jsonStr, GroceryEvent.class);
		groceryObj.getItem().setGroceryEvent(groceryObj);
		var savedGroceryEventObj = groceryEventRepoSpy.save(groceryObj);
		
		/*Step 2. modify the item name and then persist it via kafka update*/
		groceryObj.setAction(Action.update);
		groceryObj.getItem().setItemName("daliya");
		jsonStr = objMapper.writeValueAsString(groceryObj);
		
		kafkaTemplate.sendDefault(jsonStr).get();
		
		/*when*/
		CountDownLatch latch = new CountDownLatch(1);
		latch.await(5, TimeUnit.SECONDS);
		/* then */
		verify(groceryEventConsumerSpy,times(1)).onMessage(isA(ConsumerRecord.class));
		verify(groceryServiceSpy,times(1)).saveOrUpdateGroceryEvent(isA(ConsumerRecord.class));
	   List<GroceryEvent> recordList = (List<GroceryEvent>) groceryEventRepoSpy.findAll();
	   assert recordList.size() == 1;
	   var updatedGroceryEventOpt = groceryEventRepoSpy.findById(savedGroceryEventObj.getEventId());
	   Assertions.assertTrue(updatedGroceryEventOpt.isPresent());
	   assertEquals(savedGroceryEventObj.getEventId(), updatedGroceryEventOpt.get().getEventId());
	   assertEquals("daliya", updatedGroceryEventOpt.get().getItem().getItemName());
 }
 
 //@Test
 void updateGroceryEvent_Failure() throws InterruptedException, ExecutionException, JsonMappingException, JsonProcessingException {
	
	 /*given*/
	 
		String jsonStr = "{\"action\":\"update\",\"item\": {\"itemName\": \"oats\",\"itemPrice\": 55.439999999}}";
		
		kafkaTemplate.sendDefault(jsonStr).get();
		
		/*when*/
		CountDownLatch latch = new CountDownLatch(1);
		latch.await(3, TimeUnit.SECONDS);
		/* then */
		
		/*1. in case of failure it tries 6 times by default
		 *2. in case of custom error and backoff, specify the retry accordingly times(<custom backoff>) 
		 * */
		verify(groceryEventConsumerSpy,times(5)).onMessage(isA(ConsumerRecord.class));
		verify(groceryServiceSpy,times(5)).saveOrUpdateGroceryEvent(isA(ConsumerRecord.class));
 }
 
 //@Test
 void testRecordPublishedOnRetryTopic() throws InterruptedException, ExecutionException, JsonMappingException, JsonProcessingException {
	
	 /* step-1 publish the message on grocery-event topic */
	 /* step-2 consume the message and check if eventid is 434 explicitly throw RecoverableDataAccess*/
	 /* step-3 publish the message on retry topic (for this code is written in GroceryConsumerConfig.java class 5.1) */
	 /* step-4 Consume the message from retry topic
	  * 
	  */
	 /*given*/
		
		String jsonStr = "{\"eventId\":434,\"action\":\"update\",\"item\": {\"itemName\": \"daliya\",\"itemPrice\": 55.439999999}}";
		kafkaTemplate.sendDefault(jsonStr).get();
		
		/*when*/
		CountDownLatch latch = new CountDownLatch(1);
		latch.await(3, TimeUnit.SECONDS);
		/* then */
		
		verify(groceryEventConsumerSpy,times(3)).onMessage(isA(ConsumerRecord.class));
		verify(groceryServiceSpy,times(3)).saveOrUpdateGroceryEvent(isA(ConsumerRecord.class));
		Map<String, Object> config = new HashMap<>(
				KafkaTestUtils.consumerProps("cons-g-1", "true", embeddedKafkaBroker));
		consumer = new DefaultKafkaConsumerFactory<>(config, new IntegerDeserializer(), new StringDeserializer())
				.createConsumer();
		embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, retryTopicName);
		
		ConsumerRecord<Integer, String> record = KafkaTestUtils.getSingleRecord(consumer, retryTopicName);
		assertEquals(jsonStr, record.value());
		
 }
 
 //@Test
 void testRecordPublishedOnDLTTopic() throws InterruptedException, ExecutionException, JsonMappingException, JsonProcessingException {
	
	 /* step-1 publish the message on grocery-event topic */
	 /* step-2 consume the message and check if eventid is 434 explicitly throw RecoverableDataAccess*/
	 /* step-3 publish the message on deadletter topic (for this code is written in GroceryConsumerConfig.java class 5.1) */
	 /* step-4 Consume the message from deadletter topic
	  * 
	  */
	 /*given*/
		
		String jsonStr = "{\"eventId\":111,\"action\":\"update\",\"item\": {\"itemName\": \"daliya\",\"itemPrice\": 55.439999999}}";
		kafkaTemplate.sendDefault(jsonStr).get();
		
		/*when*/
		CountDownLatch latch = new CountDownLatch(1);
		latch.await(3, TimeUnit.SECONDS);
		/* then */
		
		verify(groceryEventConsumerSpy,times(1)).onMessage(isA(ConsumerRecord.class));
		verify(groceryServiceSpy,times(1)).saveOrUpdateGroceryEvent(isA(ConsumerRecord.class));
		Map<String, Object> config = new HashMap<>(
				KafkaTestUtils.consumerProps("cons-g-1", "true", embeddedKafkaBroker));
		consumer = new DefaultKafkaConsumerFactory<>(config, new IntegerDeserializer(), new StringDeserializer())
				.createConsumer();
		embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, deadLetterTopicName);
		
		ConsumerRecord<Integer, String> record = KafkaTestUtils.getSingleRecord(consumer, deadLetterTopicName);
		assertEquals(jsonStr, record.value());
		
 }
 
/* 5.2 Recovery -> test if record persisted in db*/
 @Test
 void testRecordPersistedInDB() throws InterruptedException, ExecutionException, JsonMappingException, JsonProcessingException {
	
	 
	 /*given*/
		
		String jsonStr = "{\"eventId\":111,\"action\":\"update\",\"item\": {\"itemName\": \"daliya\",\"itemPrice\": 55.439999999}}";
		kafkaTemplate.sendDefault(jsonStr).get();
		
		/*when*/
		CountDownLatch latch = new CountDownLatch(1);
		latch.await(3, TimeUnit.SECONDS);
		/* then */
		assert failledRecordLogsRepo.count()>0;
		//assertEquals(jsonStr, record.value());
		
 }
}
