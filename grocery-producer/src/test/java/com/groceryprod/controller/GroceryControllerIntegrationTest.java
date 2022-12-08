package com.groceryprod.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import com.groceryprod.event.GroceryEvent;
import com.groceryprod.model.Item;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = { "grocery-event" }, partitions = 3)
/*
 * TestPropertySource will override the broker property configured in
 * application.prop file ${spring.embedded.kafka.brokers} - value take
 * from @link{EmbeddedKafkaBroker.class} file
 */

@TestPropertySource(properties = { "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
		"spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}" })
public class GroceryControllerIntegrationTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private EmbeddedKafkaBroker embeddedKafkaBroker;

	private Consumer<Integer, String> consumer;

	@BeforeEach
	public void init() {
		Map<String, Object> config = new HashMap<>(
				KafkaTestUtils.consumerProps("cons-g-1", "true", embeddedKafkaBroker));
		consumer = new DefaultKafkaConsumerFactory<>(config, new IntegerDeserializer(), new StringDeserializer())
				.createConsumer();
		embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
	}

	@AfterEach
	public void tearDown() {
		consumer.close();
	}

	@Test
	public void produceGroceryEvent() throws InterruptedException {

		/* given */
		Item item = Item.builder().itemId(null).itemName("integration test").itemPrice(new BigDecimal(55.454)).build();

		GroceryEvent event = GroceryEvent.builder().eventId(null).item(item).build();
		HttpHeaders headers = new HttpHeaders();
		headers.set("content-type", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<GroceryEvent> entity = new HttpEntity<GroceryEvent>(event, headers);
		/* when */
		ResponseEntity<GroceryEvent> responseEntity = testRestTemplate.exchange("/v1.0/grocery/event", HttpMethod.POST,
				entity, GroceryEvent.class);
		/* then */
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
	    Thread.sleep(5000);
		ConsumerRecord<Integer, String> record = KafkaTestUtils.getSingleRecord(consumer, "grocery-event");
	    String value = record.value();
	    String expectedRecord = "{\"eventId\":null,\"item\":{\"itemId\":null,\"itemName\":\"integration test\",\"itemPrice\":55.45400000000000062527760746888816356658935546875}}";
	    assertEquals(expectedRecord, value);
	    log.info("actual record is : {}",value);
	}
}
