package com.groceryprod.model;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Item {
	
	@NotNull
	private Integer itemId;
	
	@NotBlank
	private String itemName;
	private BigDecimal itemPrice;
	
}
