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
  - Follow below stesps to configure consumer in kafka
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
         
  
