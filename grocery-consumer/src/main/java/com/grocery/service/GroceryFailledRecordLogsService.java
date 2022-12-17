package com.grocery.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.grocery.entity.GroceryFailledRecordLogs;

public interface GroceryFailledRecordLogsService {

	void processFailledRecord(ConsumerRecord<Integer, String> r, Exception e, int i);

	Iterable<GroceryFailledRecordLogs> findAllByRecStatus(int i);
	
	

}
