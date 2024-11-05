- [introduction](#introduction)
- [Kafka Terminology](#kafka-terminology)
- [Partitioning in apache kafka](#partitioning-in-apache-kafka)
   -[How Partitioning happens](#how-partitioning-happens)
   -[Data Balancing](#data-balancing)
- [Changing Partition Later](#changing-partition-later)
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
<img src="/partition.png" width="500"/>

- Partitioning is a mechanism to distribute topic data across multiple brokers.
- Or we can say Partitioning is a way to parallelise production and consumption of messages.
- **how many partition one should create?**
   - We can decide with target/consumer read capacity.
     ```if target read capacity = 2GiB/sec
        consumer throughput = 200MiB/sec
        then number of consumers required would be 2000/200 = 10 consumer. therefore number of partition needed here is 10.
     ```
### How partitioning happens
- **Default partitioner** ---> default partiner uses 3 way to partition data.
  1. Explicitly specifying partition--> Not recommended
  2. Using key or hash based partitioner--> Maintains production and consumption of message at partition level, if you change the number of partion later ordering might be impacted. If u don't care about ordering we are good to add additional partition.
  3.  round robin/sticky partitioner--> Send a batch for every partition. It selects a partition and continue assigning record to a batch until one of two condition happens, either we reach the max number of batch size or the time we allocated for that operation elasped.
### Data Balancing
- **Unbalanced partition**---> arises when data distribution does not happens evenly.
   - Because of unbalanced partition(hot partition) Producer and consumer will become slow.
  
 ```
   Partition 1-> -----------------48%----------------
   Partition 2->----------20%-------------
   Partition 3->-----10%-------
   Partition 4->----5%----

 ```
- **why unbalancing happens? and how we can prevent this issue**
   - First reason is selection of key, if proper key is not selected data would be distributed unevenly across the partition.
   - Data is not evenly produced over time(peak hours and inactivity)
   - If your Broker1 is slow, and lets assume we are sending batch to broker 1, since broker is slow so it might take time to send ack to producer in between that time producer might accumulate more data and send it to broker1. that way we would end up with hot partition, which will make broker 1 even more slow.
- **Solving unbalanced partition issue(sticky partitioner)**
   - partitioner.adaptive.partitioning.enabled=true(producer will adapt the performance of broker and send more record to broker which is faster)
   - partitioner.availability.timeout.ms>0
## Changing Partition Later
- Its not possible to decrease number of partition in existing topic.
- However we can add additional partition to topic. But again that depends.
## Performance
- **Throughput**--> Number of messages that goes through the system in a given amount of time.
- **Latency**--> Overall time it takes to process each message.
- **Lag**--> The delta between the last produced message and last consumer's committed message.
