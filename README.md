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
	
