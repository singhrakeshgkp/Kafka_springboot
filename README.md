# Kafka Setup 
- Download apache kakfka from official website.
 <details><summary>Windows</summary>
 <p>
 
 - Navigate to **bin/windows** directory.
 
 ### Start Zookeeper and Kafka Broker
  
  - Start zookeeper using below command.
  	```
	zookeeper-server-start.bat ..\..\config\zookeeper.properties
	```
  - Follow below steps in order to start broker
  
   	- Add below properties in server.properties
	
	```
	listeners=PLAINTEXT://:9092
	auto.create.topics.enable=false
	```
	- Now run below command.
	```
	kafka-server-start.bat ..\..\config\server.properties
	```
### Create Topic, Produce and consume message using CLI
	 
  - Make sure zookeeper and kafka broker is up and running
	 
    - Use below command to create new topic this is applicable for kafka version>= 2.13 .
	 ```
	 kafka-topics.bat --bootstrap-server localhost:9092 --topic myfirsttopic --create --partitions 3 --replication-factor 1
	 ```
  - Instantiate a console producer
	   - Without key.
	 ```
	 kafka-console-producer.bat --broker-list localhost:9092 --topic myfirsttopic
	 ```
	 - With Key
	 
 </p>
 </details>
 
 	
	
		
# Kafka Terminology
 1. **Topic**  
 	a)Topic is an entity is kafka with a name.
	b) It is created inside kafka broker.
	c) Producer and consumer uses topic name to produce and consume the messages.
	d) Even though consumer consumes the messages, it still resides in kafka broker as per the defined retention time.
	e)
2. **Partition**
	a) Partition is where messages live inside topic.
	b) Each topic can have one or more parititons.
	c)
		
# Configure Kafka Template
  ### Mandatory Values
  	bootstrap-servers: localhost:9090,localhost:9093,localhost:9094
	key-serializer: org.apache.kafka.common.serialization.IntegerSerializer
	value-serializer: org.apache.kafka.common.serialization.StringSerializer
### Creating producer programmatically
	add following properties in application.yml file
	add new config class and create followin bin(AutoCreateConfig)
Now run spring boot application topic should be created programmatically. to see the topic list use below command in windows
 kafka-topics.bat --list --zookeeper localhost:2181 (if u are using linux u can refer kafka-topics.sh file)
	
# Integration Testing Producer
### Embedded kafka
 usefull in integration testing(while running ci/cd piline it won't connect to actual server it will connect to embedded kafka)
 perform below steps to integrate embedded kafka with your test cases.
 1. specify below properties on top of your kafkatestcontroller class
 2. @EmbeddedKafka(topics = {"library-events"},partitions = 3)
   @TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
   ,"spring.kafka.admin.properties.bootstrap-servers=${spring.embedded.kafka.brokers}" - 
   This will overide bootstrap server and admin server prop given in  application.prop or yml file
   ${spring.embedded.kafka.brokers}  - this one is taken from EmbeddedKfafkaBroker class file.
   
   
# Kafka Consumer
 ### Comitting offeset manually
 1. copy and paste following bean from KafkaAnnotationDrivenConfiguration to LibraryEventConsumerConfig.
  @Bean
	@ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
	ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
			ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
			ConsumerFactory<Object, Object> kafkaConsumerFactory) {
		ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		configurer.configure(factory, kafkaConsumerFactory);
		factory.getContainerProperties().setAckMode(AckMode.MANUAL);
		return factory;
	}
2. Create a new consumer class "LibraryEventConsumerManualOffset" and implement AcknowledgingMessageListener interface 
3. override OnMessage method and annotate it with @KafkaListener annotation

### Integration Testing consumer
 1. Create an integration test class and annotate the class with following annotation
  @SpringBootTest
  @EmbeddedKafka(topics = {"library-events"},partitions = 3)
  @TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
		"spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
 Note:- overriden the prop in application.yml file for testing
 2. Add following prop in application.yml
    kafka:
    template:
      default-topic: library-events
  producer:
      bootstrap-servers: localhost:9092, localhost:9093, localhost:9094
      key-serializer: org.apache.kafka.common.serialization.IntegerSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      
 #How to write integration test using testcontainer?????
 
 
### Consumer  customizing default error handling behavior 
 1. Go to LibraryEventConfig. and set factory.setCommonErrorHandler(instance)- instance of defaulterrorhandler

### Consumer  Add a retry Listener to monitor each retry attempt
1. setRetryListener in error handler in LibraryEventConfig file.

### Consumer  Retry the specific exception
1. specify the list of exception for which it should not go for retry.
2. add below code.
  errorHandler.addNotRetryableExceptions();
 exceptionToIgnore.forEach(errorHandler::addNotRetryableExceptions);

### Retry failled record with ExponentialBackOff
### Recovery in kafka consumer
1. define below topics in application.yml file
 topics: 
  retry: 'iibrary-events-RETRY'
  dlt: 'iibrary-events-DLT' 
2. define below method in libraryEventConsumerConfig class
     @Autowired
	KafkaTemplate kafkaTemplate;

	@Value(("${topics.retry}"))
	private String retryTopic;
	
	@Value(("${topics.dlt}"))
	private String retryDLT;
	
	public DeadLetterPublishingRecoverer publishRecoverer() {

		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate, (r, e) -> {
			if (e instanceof RecoverableDataAccessException) {
				return new TopicPartition(retryTopic, r.partition());
			} else {
				return new TopicPartition(retryDLT, r.partition());
			}
		});
		return recoverer;
	}
	
3. add recoverer in error handler
   var errorHandler = new DefaultErrorHandler(publishRecoverer(),exponentialBackOff);
4. Verify it using integration test ending with 333_libraryEvent

### Consuming the messages published on the retry topic.
1. Create new "LibraryEventRetryConsumer" class and specify the required annothation.
### Testing retry consumer
1. specify autoStartup = "{retryListener.startup:true}" prop in LibraryEventRetryListener.
2. modify the LibraryEventConsumerIntegrationTest init method
"retryListener.startup=false"
var container =	registry.getListenerContainers().stream().filter(
				(msgListenerContainer) -> msgListenerContainer.getGroupId().equalsIgnoreCase("msgListenerContainer"))
				.collect(Collectors.toList()).get(0);
	ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
 
 ### Recovery save the failed messages in DB
 1. Define following consumer record recoverer in ConsumerConfig.java class
   ConsumerRecordRecoverer  consumerRecordRecoverer = (consumerRecord,e) -> {
		log.info("exception in consumer record recoverer {}",e.getMessage());
		var record = (ConsumerRecord<Integer,String>) consumerRecord;
		if (e.getCause() instanceof RecoverableDataAccessException) {
			//recovery logic
			log.info("Inside recovery");
			failureRecordService.saveFailedRecord(record,e,RETRY);
		} else {
			//non recovery logic
			log.info("inside non recovery");
			failureRecordService.saveFailedRecord(record,e,DEAD);
		}
	};
2. Add consumer record recoverer in default error handler(remove existing publishRecoverer())
3. Create Entity calss, add attributes in the FailedRecord entity, create service and repo class as well.
4. create test in integration consumer test calss

### Recovery: Spring schedular to recover failed message
1. Annotate SpringBootApplication class with @EnableScheduling annotation
2. Create RetrySchedularClass and add required annotation.

