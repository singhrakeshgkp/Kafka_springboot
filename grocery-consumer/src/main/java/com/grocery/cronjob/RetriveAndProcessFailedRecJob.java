package com.grocery.cronjob;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.grocery.entity.GroceryFailledRecordLogs;
import com.grocery.service.GroceryFailledRecordLogsService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RetriveAndProcessFailedRecJob {

	@Autowired
	private GroceryFailledRecordLogsService failledRecordLogsService;
	@Scheduled(fixedRate = 50000)
	public void retriveAndProcessRec() {

    log.info("RetriveAndProcessFailedRecJob.retriveAndProcessRec() start");
	List<GroceryFailledRecordLogs> records =	(List<GroceryFailledRecordLogs>) failledRecordLogsService.findAllByRecStatus(1);
	log.info("Number of record is found is :- {}",records.size());
	log.info("RetriveAndProcessFailedRecJob.retriveAndProcessRec() end");
	}
}
