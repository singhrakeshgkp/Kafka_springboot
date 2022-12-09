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
  
