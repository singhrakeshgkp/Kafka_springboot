package com.groceryprod.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GroceryControllerAdvice {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleError(MethodArgumentNotValidException ex){
	 List<FieldError> fieldErrors =	ex.getBindingResult().getFieldErrors();
	 String errorMsg = fieldErrors.stream()
	 			.map(fieldError-> fieldError.getField()+"_"+fieldError.getDefaultMessage())
	 			.sorted()
	 			.collect(Collectors.joining(","));
	 log.info("error messages are : {}",errorMsg);
	 return new ResponseEntity<>(errorMsg,HttpStatus.BAD_REQUEST);
	 
	}
}
