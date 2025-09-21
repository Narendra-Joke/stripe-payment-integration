package com.cpt.payments.stripeprovider;

import lombok.Data;

@Data
public class LineItem {
	private int quantity;
	private String currency;
	private String productName;
	private double unitAmount;
}
