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
- **Topic**--> Topic is a unique name for kafka stream, its an abstract term, because on the disks it stores data in partitions.
- **Partition**-->
- **Offset**-->
- **Consumer Group**-->
- 
## Partitioning in apache kafka
- [Diagram](/partition.png)
<img src="/partition.png" width="800"/>
- Partitioning is a mechanism to distribute topic data across multiple brokers.
- Or we can say Partitioning is a way to parallelise production and consumption of messages.
- **how many partition one should create?**
   - We can decide with target/consumer reach capacity.
     ```if target read capacity = 2GiB/sec
        consumer throughput = 200MiB/sec
        then number of consumers required would be 2000/200 = 10 consumer. therefore number of partition needed here is 10.
     ```

  ## Performance
  - **Throughput**--> Number of messages that goes through the system in a given amount of time.
  - **Latency**--> Overall time it takes to process each message.
  - **Lag**--> The delta between the last produced message and last consumer's committed message.
