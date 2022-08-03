# Local Setup 
 #### Windows
 	1 Download apache kakfka from official website.
	2 Add below properties in /config/server.properties file.
	3 Navigate to /bin/windows directory and open command prompt.
	4 Run below command to start zookeeper and kafka server
		zookeeper-server-start.bat ..\..\config\zookeeper.properties
		kafka-server-start.bat ..\..\config\server.properties
		
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
	
# Integration Testing
### Embedded kafka
 usefull in integration testing(while running ci/cd piline it won't connect to actual server it will connect to embedded kafka)
 perform below steps to integrate embedded kafka with your test cases.
 1. specify below properties on top of your kafkatestcontroller class
 2. @EmbeddedKafka(topics = {"library-events"},partitions = 3)
   @TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
   ,"spring.kafka.admin.properties.bootstrap-servers=${spring.embedded.kafka.brokers}" - 
   This will overide bootstrap server and admin server prop given in  application.prop or yml file
   ${spring.embedded.kafka.brokers}  - this one is taken from EmbeddedKfafkaBroker class file.
   
   
