# Local Setup 
 #### Windows
 	1 Download apache kakfka from official website.
	2 Add below properties in /config/server.properties file.
	3 Navigate to /bin/windows directory and open command prompt.
	4 Run below command to start zookeeper and kafka server
		zookeeper-server-start.bat ..\..\config\zookeeper.properties
		kafka-server-start.bat ..\..\config\server.properties
    
 ### Starting multiple broker
    1. Create multiple server.properties file such as server1, server2, server3 each file will represent a broker.
    2. Create new directory for logs such as broker2logs.
    2. update following values as per your server for example if u are configuring server2 then
         broker.id=2 (it should be unique)
         listeners=PLAINTEXT://localhost:9093,
         log.dirs=../broker2logs
   3. Now start the server(while starting the server specify the respective server.properties file)
   
 ### starting consumer console
   use below command to start consumer console
   kafka-console-consumer.bat --bootstrap-server localhost:9092(kafka-server host) --topic library-events(topic name)
         
