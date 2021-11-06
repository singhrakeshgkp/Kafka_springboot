# Local Setup 
 #### Windows
 	1 Download apache kakfka from official website.
	2 Add below properties in /config/server.properties file.
	3 Navigate to /bin/windows directory and open command prompt.
	4 Run below command to start zookeeper and kafka server
		zookeeper-server-start.bat ..\..\config\zookeeper.properties
		kafka-server-start.bat ..\..\config\server.properties
