package com.groceryprod.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Item {
	
	private Integer itemId;
	private String itemName;
	private BigDecimal itemPrice;
	
}
