package com.grocery.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "grocery_event")
public class GroceryEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer eventId;
	
	@Column(name="action")
	@Enumerated(EnumType.STRING)
	private Action action;
	
	@OneToOne(mappedBy = "groceryEvent", cascade = {CascadeType.ALL})
	private Item item;
	
}
