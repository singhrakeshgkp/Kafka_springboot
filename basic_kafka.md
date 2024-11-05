- [introduction](#introduction)
- [Kafka Terminology](#kafka-terminology)
- [Partitioning in apache kafka](#partitioning-in-apache-kafka)
- [jkf](#kdfjd)

## Introduction
- Apache Kafka is an open-source data streaming platform that collects, stores, and processes data in real time.
- Here is the basic diagram of apache kafka


## Kafka Terminology
- **Zookeeper**->
- **Producer** -> An application that sends messages to kafka
- **message**--> Small to medium sized piece of data. For kafka every message is an array of bytes.
- **Consumer**--> An application that reads data from kafka.
- **Broker**--> is a kafka server.
- **Cluster**--> A group of brokers/computer sharing workloads for a common purpose.
- **Topic**--> Topic is a unique name for kafka stream, its an abstract term because on the disks it stored data in partitions.
- **Partition**-->
- **Offset**-->
- **Consumer Group**-->
- 
## Partitioning in apache kafka
- producer pushes/send the data to topic, topic is a abstract term because on the disks it stored data in partitions. Pari
