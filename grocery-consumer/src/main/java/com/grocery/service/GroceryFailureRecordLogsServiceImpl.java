package com.grocery.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grocery.entity.GroceryFailledRecordLogs;
import com.grocery.repo.GroceryFailledRecordLogsRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GroceryFailureRecordLogsServiceImpl implements GroceryFailledRecordLogsService {

	@Autowired
	GroceryFailledRecordLogsRepo failledRecordLogsRepo;

	@Override
	public void processFailledRecord(ConsumerRecord<Integer, String> r, Exception e, int i) {
		
		var failledRecord = new GroceryFailledRecordLogs(null, r.topic(), r.key(), r.value(), i, r.offset(), r.partition());
		failledRecordLogsRepo.save(failledRecord);
	}

	@Override
	public Iterable<GroceryFailledRecordLogs> findAllByRecStatus(int i) {
		// TODO Auto-generated method stub
		return failledRecordLogsRepo.findAllByRecStatus(i);
	}
	
	
}
