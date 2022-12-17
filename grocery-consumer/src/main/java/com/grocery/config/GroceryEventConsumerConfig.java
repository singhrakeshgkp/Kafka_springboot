package com.grocery.config;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import com.grocery.service.GroceryFailledRecordLogsService;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;


/*@EnableKafka annotation will allow the auto configuration
 *to read the value form prop file
 * */
@Configuration
@EnableKafka
@Slf4j
public class GroceryEventConsumerConfig {

	@Autowired 
	KafkaProperties properties;
	
	@Autowired
	KafkaTemplate<Integer, String> template;
	
	@Value("${topics.retry}")
	private String retryTopicName;
	
	@Value("${topics.dlt}")
	private String deadLetterTopicName;
	
	@Autowired
	GroceryFailledRecordLogsService failledRecordLogsService;
	
	@Bean
	@ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
	ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
			ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
			ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
		ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		configurer.configure(factory, kafkaConsumerFactory
				.getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(this.properties.buildConsumerProperties())));
		
		/*concurrency not recommended for application running on cloud*/
		//factory.setConcurrency(3);
		factory.setCommonErrorHandler(getErrorHandler());
		return factory;
	}
	
	public DefaultErrorHandler getErrorHandler() {
		
		/* 1. Custom Error Handler */
		/*var fixedBackOff = new FixedBackOff(1000L, 3);
		return new DefaultErrorHandler(fixedBackOff);*/
		
		/* 2. Add RetryListener to monitor each retry attempt */
		/*
		 * var fixedBackOff = new FixedBackOff(1000L, 3); var defaultErrorHandler = new
		 * DefaultErrorHandler(fixedBackOff);
		 * defaultErrorHandler.setRetryListeners((consumerRec, ex, attempt)->{ log.
		 * info("GroceryEventConsumerConfig.getErrorHandler() exception {}, delivery attempt {}"
		 * ,ex.getMessage(),attempt); });
		 */
		
		/* 3. Retry specific exception by defining custom policy */
		
		/*
		 * var fixedBackOff = new FixedBackOff(1000L, 3); var defaultErrorHandler = new
		 * DefaultErrorHandler(fixedBackOff);
		 * defaultErrorHandler.setRetryListeners((consumerRec, ex, attempt)->{ log.
		 * info("GroceryEventConsumerConfig.getErrorHandler() exception {}, delivery attempt {}"
		 * ,ex.getMessage(),attempt); }); var exceptionsToIgnore =
		 * List.of(IllegalArgumentException.class ,NullPointerException.class);
		 * exceptionsToIgnore.forEach(defaultErrorHandler :: addNotRetryableExceptions);
		 */
		
		/* 4. Retries with Exponential backoff*/ 
		/*
		 * var expBackOff = new ExponentialBackOff(1000L, 2);
		 * expBackOff.setMaxInterval(200000L); var defaultErrorHandler = new
		 * DefaultErrorHandler(expBackOff);
		 * defaultErrorHandler.setRetryListeners((consumerRec, ex, attempt)->{ log.
		 * info("GroceryEventConsumerConfig.getErrorHandler() exception {}, delivery attempt {}"
		 * ,ex.getMessage(),attempt); });
		 */
				
		/* 5.1. Recovery - Publish message to retry topic*/ 
		
		var fixedBackOff = new FixedBackOff(1000L, 3);
		/*
		 * DeadLetterPublisher Recoverer is required while publishing the record on
		 * retry or dlt topic hence commented out below code
		 */
		
		/*
		 * var defaultErrorHandler = new DefaultErrorHandler( getRecoverer(),
		 * fixedBackOff );
		 */
		
		
		/* 5.2. start Recovery - store failed record in database*/
		ConsumerRecordRecoverer recoverer = ((r, e) ->{
		
	            if (e instanceof RecoverableDataAccessException ||  e.getCause() instanceof RecoverableDataAccessException) {
	            	log.info("inside RecoverableDataAccessException publishing msg on retrytopic");
				/*Recoverable Logic*/
	            	failledRecordLogsService.processFailledRecord((ConsumerRecord<Integer, String>) r,e,1);
	            }
	            else {
				/*Non Recoverable logic*/
	            	failledRecordLogsService.processFailledRecord((ConsumerRecord<Integer, String>) r,e,-1);
	            }
			
		});
		  var defaultErrorHandler = new DefaultErrorHandler(
				  //getRecoverer(),
				  recoverer,
				  fixedBackOff
				  );
		  
		  /* 5.2. end */
		  
		  
		  var notRetryableException = List.of(IllegalArgumentException.class, EntityNotFoundException.class);
		  var retryableException = List.of(RecoverableDataAccessException.class);
		  notRetryableException.forEach(defaultErrorHandler :: addNotRetryableExceptions);
		  retryableException.forEach(defaultErrorHandler :: addRetryableExceptions);
		defaultErrorHandler.setRetryListeners((consumerRec, ex, attempt) -> {
			ex = (Exception) ex.getCause();
			log.info("GroceryEventConsumerConfig.getErrorHandler() exception {}, delivery attempt {}",
					ex.getMessage(), attempt);
			
		});
		  
	  
	return defaultErrorHandler;
	}
	
	/* 5.1. publish the failed message on retry topic*/
	public DeadLetterPublishingRecoverer getRecoverer() {
		
		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
		        (r, e) -> {
		            if (e instanceof RecoverableDataAccessException ||  e.getCause() instanceof RecoverableDataAccessException) {
		            	log.info("inside RecoverableDataAccessException publishing msg on retrytopic");
		                return new TopicPartition(retryTopicName, r.partition());
		              
		            }
		            else {
		                return new TopicPartition(deadLetterTopicName, r.partition());
		            }
		        });
		return recoverer;
	}
	
	
}
