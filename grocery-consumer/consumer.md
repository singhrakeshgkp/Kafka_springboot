# Creating consumer using spring boot

<details><b><summary>Configuring Kafka Consumer and Reading Record from topic</b></summary>
 <p>
   
  - Create new spring boot application with following dependency.
     - Kafka for spring
     - H2 Database
     - Spring data jpa
     - Lombock
 - Things required to configure kafka consumer
   - MessageLitenerContainer  
    - KafkaMessageLitenerContainer
       - Polls the record
       - commit the offsets
       - single threaded
    - ConcurrentMessageListenerContainer
       - Represent multiple LaflaMessageListenerContainer
  - @KafkaListener annotation
    - It uses the concurrentmessageListenercontainer internally
    - Simplest way to configure kafka consumer
  - <b>Follow below stesps to configure kafka consumer in your spring boot application </b>
    - Specify below properties in application.prop file
       ```
      server.port=8081
      spring.kafka.bootstrap-servers=localhost:9094, localhost:9093
      spring.kafka.consumer.key-deserializer = org.apache.kafka.common.serialization.IntegerDeserializer
      spring.kafka.consumer.value-deserializer = org.apache.kafka.common.serialization.StringDeserializer
      spring.kafka.consumer.group-id = grocery-event-group
       ```
    - Create a new Class named GroceryEventConsumerConfig, annotate it with @Configuration and @EnableKafka annotations
    - Create a new bean class as shown below<br/>
      ```
        @Component
        @Slf4j
        public class GroceryEventConsumer {

        /*Here we are going to get the record in ConsumerRecord obj, while producing we passed producer record
         *can pass multiple topic
         * */
        @KafkaListener(topics = {"grocery-event"})
        public void onMessage(ConsumerRecord<Integer, String> consumerRecord) {
          log.info("records is {}",consumerRecord);
        }
        }
      ```
  - <b>Consumer Group, Rebalance</b>
   - Consumer Group -> Running multiple instances of the same application with same group id
   - Rebalance -> Changing partition ownership form one to another
 - <b>Committing Offsets manually- Pending Not covered yet</b> for more details please check kafka ref doc.
   -  kdsfk
   -  sdfsf
   -  fsdfsf
   -  fsdfsff
 - <b>Concurrent Consumer</b> - Spin up multiple listener container.
   - define below Bean in ```GroceryEventConsumerConfig.java``` file and set ```factory.setConcurrency(int value) ex. 2,3,4,...etc```
     ```
      @Bean
      @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
      ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
          ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
          ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory
            .getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties())));

        /*concurrency not recommended for application running on cloud*/
        factory.setConcurrency(3);
        return factory;
      }
     ```
     - Now restart the server, you will observe in console pointing multiple listener running in different thread.
  
</p>
</details>
  
<details><summary><b>Persisting data from kafka topic to database </b></summary>
<p>

- Configure h2 database
  - use the below properties in your application.prop or yml file
  ```
  spring.datasource.url = jdbc:h2:mem:grocerydb
  spring.sql.init.username = sa
  spring.sql.init.password=
  spring.datasource.driver-class-name = org.h2.Driver
  spring.jpa.database = h2
  spring.jpa.database-platform = org.hibernate.dialect.H2Dialect
  spring.jpa.generate-ddl = true
  spring.jpa.show-sql = true
  spring.h2.console.enabled = true
  ```
  - To access console from browser add spring boot web starter dependency in pom.xml file
  - Run your application and try to access localhost:/<port>/h2-console, in case of application context, append context name after port .
  - Create Repo and Service classes, call it from consumer onmessage method. 
</p> 
</details>   
  
<details><b><summary>Kafka Consumer Integration test</b></summary>
 
<p>
 
  - Define following properties in application.prop or yml file
  ```
  spring.kafka.producer.key-serializer = org.apache.kafka.common.serialization.IntegerSerializer
  spring.kafka.producer.value-serializer = org.apache.kafka.common.serialization.StringSerializer
  spring.kafka.template.default-topic=grocery-event
  spring.kafka.producer.bootstrap-servers=localhost:9094, localhost:9093

  ```
  - Create Test class ``` ConsumerIntegTest.java  ``` and define all the required test method.
 
 
</p>
</details>
  
</details>
<details><b><summary>Testing using real database (not in memory db such as H2)</b></summary>
 
<p>
 
- Pending comming soon
- Testing spring boot application by andy william
 
 
 
</p>
</details>
  
<details><b><summary>Error Handling, Retry and Recovery</b></summary>
 
<p>
 
- Custom Error Handler and custom retry in kafka consumer
  - Go to GroceryEventConsumerConfig.java class
  - Set commonerrorhandler
  - create a new method, this method will retunr DefaultErrorHandler with backoff of max 3 retry, and 1 second interval
  ``` 
  /*concurrency not recommended for application running on cloud*/
		factory.setConcurrency(3);
		factory.setCommonErrorHandler(null);
  ```
 ```
  public DefaultErrorHandler getErrorHandler() {
		var fixedBackOff = new FixedBackOff(1000L, 3);
		return new DefaultErrorHandler(fixedBackOff);
	}
 ```
- Add a RetryListener to monitor each retry attempt
	
	```
	var defaultErrorHandler = new DefaultErrorHandler(fixedBackOff);
		defaultErrorHandler.setRetryListeners((consumerRec, ex, attempt)->{
			log.info("GroceryEventConsumerConfig.getErrorHandler() exception {}, delivery attempt {}",ex.getMessage(),attempt);
		});
	```
- Retry specific exception by defining custom policy
	- Declare a list of non retryable exception and add it to exception handler.
	```
	var exceptionsToIgnore = List.of(IllegalArgumentException.class
				 ,NullPointerException.class);
		 exceptionsToIgnore.forEach(defaultErrorHandler :: addNotRetryableExceptions);
	```
- Retry Failed record with Exponential BackOff
	- Use replace fixedbackoff with expbackoff as shown below
	```
	/* step 4. Retries with Exponential backoff*/ 
		var expBackOff = new ExponentialBackOffWithMaxRetries(5);
		expBackOff.setInitialInterval(1000L);
		expBackOff.setMultiplier(2.0);
		expBackOff.setMaxInterval(10000L);
		var defaultErrorHandler = new
	  DefaultErrorHandler(expBackOff);
	  defaultErrorHandler.setRetryListeners((consumerRec, ex, attempt)->{ log.
	  info("GroceryEventConsumerConfig.getErrorHandler() exception {}, delivery attempt {}"
	  ,ex.getMessage(),attempt); });
	```
- Recovery in kafka consumer
	- <b>Approach 1 </b>
	  - publish the failed message to retry topic
	    - Define following method in your consumer config class
	    ```
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
	    ```
	    - Define two topics retry and dlt in application.prop file. explicitly throw RecoverableDataAccessException from service layer and test it        		      using integration test
	
	    ```
	    topics.retry=grocery-event.retry
	    topics.dlt=grocery-event.dlt
	    ```
	    - Run the Integration test, consumer should be able to read the messages from retry topic
	    - <b>Create Listener for retry topic to consume the messages.</b>
	
	  - save the failed message in db and retry with an schedular
       - <b>Approach 2 </b>
	 - Publish the failed record to dead later topic for tracking purposes
	 - Save the failed record into db for tracking purposes
</p>
</details>




