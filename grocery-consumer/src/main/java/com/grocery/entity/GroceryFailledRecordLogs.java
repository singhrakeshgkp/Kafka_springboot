package com.grocery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "grocery_failled_rec")
public class GroceryFailledRecordLogs {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "topic")
	private String topic;
	
	@Column(name = "rec_Key")
	private Integer key;
	
	@Column(name = "rec_value")
	private String value;
	
	@Column(name = "rec_status")
	private Integer recStatus;
	
	@Column(name = "rec_offsets")
	private Long offsets;
	
	@Column(name = "rec_partition")
	private Integer partition;
	
	
	
	
}
