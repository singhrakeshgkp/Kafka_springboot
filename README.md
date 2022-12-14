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
	 kafka-topics.bat --bootstrap-server localhost:9092 --topic myfirsttopic --create  --replication-factor 3 --partitions 4
	 ```
  - Instantiate a console producer
	   - If we do not pass key, the messages will be sent to different parition and you will not get the messages in order, if we want to maintian order we have to pass the key so that message get the same partiton.
	   - Without key
	 ```
	 kafka-console-producer.bat --broker-list localhost:9092 --topic myfirsttopic
	 ```
	 - With Key
	 ```
	kafka-console-producer.bat --broker-list localhost:9092 --topic myfirsttopic --property parse.key=true --property key.separator=,
	 ```
- Instantiate a console consumer (--from-beginning is used to read the past messaged as well if we do not use it then it will read only future messages)
	 - Without key
	 ```
	 kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic myfirsttopic --from-beginning
	 ```
	 - With key
	 ```
	 kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic myfirsttopic --from-beginning  --property print.key=true --property key.separator=,
	 ```	 	 
 </p>
 </details>
 
 
 # Advanced Kafka CLI Operation
 <details><summary>Windows</summary>
<p>
	 
 - List down topics in a cluster
 ```
kafka-topics.bat --bootstrap-server localhost:9092 --list
```
- Describe topic, by default it describe all the topic, but if we specify topic name it will show details of specified topic only.
```
kafka-topics.bat --bootstrap-server localhost:9092 --describe
kafka-topics.bat --bootstrap-server localhost:9092 --describe <topic-name>
```
- View Consumer group
```
kafka-consumer-groups.bat --bootstrap-server localhost:9092 --list
```
- Create Consumer in a specific group
```
kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic myfirsttopic --group <consumer group name>
```
- Analyzing commit logs
```
>kafka-run-class.bat kafka.tools.DumpLogSegments --deep-iteration --files /tmp/kafka-logs/myfirsttopic-0/00000000000000000000.log
```
	 
</p>
</details>
 	
# Setting up multiple kafka brokers
 <details><summary>Windows</summary>
<p>
	 
 - New server.properties file with new brokers details need to be created
 ```
broker.id=<unique broker id>
listeners=PLIAINTEXT:localhost:<unique port>
log.dirs = <log directory>
auto.create.topics.enable = false<optional value>
```
- Use below command to create topic on all the brokers
```
kafka-topics.bat -bootstrap-server localhost:9092 localhost:9093 localhost:9094 -topic test-topic -create --replication-factor 3 --partitions 4
```
- Crea
	 
</p>
</details>	

# Grocery Producer.
  (for more details click  [here](https://github.com/singhrakeshgkp/Kafka_springboot/blob/main/grocery-producer/producer.md)).
	
# Grocery Consumer.
  (for more details click  [here](https://github.com/singhrakeshgkp/Kafka_springboot/blob/main/grocery-producer/consumer.md)).  
  
# Configuring SSL.
  (for more details click  [here](https://github.com/singhrakeshgkp/Kafka_springboot/kafkassl.md)).
