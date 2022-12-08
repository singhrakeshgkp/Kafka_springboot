package com.groceryprod.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class GroceryConfig {

	@Bean
	public NewTopic groceryEvent() {
		return TopicBuilder.name("grocery-event")
					.partitions(2)
					.replicas(2)
					.build();
	}
}
