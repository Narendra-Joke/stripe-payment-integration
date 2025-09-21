package com.cpt.payments.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class LineItemDTO {
	private int quantity;
	private String currency;
	private String productName;
	private double unitAmount;	
}
