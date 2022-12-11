# Creating consumer using spring boot

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
  
  
