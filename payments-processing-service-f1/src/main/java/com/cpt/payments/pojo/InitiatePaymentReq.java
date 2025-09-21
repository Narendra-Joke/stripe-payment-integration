package com.cpt.payments.pojo;

import java.util.List;

import lombok.Data;

@Data
public class InitiatePaymentReq {

	private List<LineItem> lineItem;

	private String successUrl;
	private String cancelUrl;

}
