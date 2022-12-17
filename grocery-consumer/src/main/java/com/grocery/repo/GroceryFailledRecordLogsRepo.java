package com.grocery.repo;

import org.springframework.data.repository.CrudRepository;

import com.grocery.entity.GroceryFailledRecordLogs;

public interface GroceryFailledRecordLogsRepo extends CrudRepository<GroceryFailledRecordLogs, Integer>{

	Iterable<GroceryFailledRecordLogs> findAllByRecStatus(int i);

}
