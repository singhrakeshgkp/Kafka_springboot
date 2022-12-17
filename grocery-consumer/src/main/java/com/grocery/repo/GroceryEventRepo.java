package com.grocery.repo;

import org.springframework.data.repository.CrudRepository;

import com.grocery.entity.GroceryEvent;

public interface GroceryEventRepo extends CrudRepository<GroceryEvent, Integer>{

}
