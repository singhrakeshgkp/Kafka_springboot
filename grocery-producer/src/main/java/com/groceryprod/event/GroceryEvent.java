package com.groceryprod.event;

import com.groceryprod.model.Item;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroceryEvent {

	private Integer eventId;
	
	private Action action;
	
	//@Valid
	private Item item;
}


