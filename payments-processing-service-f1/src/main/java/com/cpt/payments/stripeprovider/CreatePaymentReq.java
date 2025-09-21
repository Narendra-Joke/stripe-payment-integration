package com.cpt.payments.stripeprovider;

import java.util.List;

import lombok.Data;

@Data
public class CreatePaymentReq {

	private String txnRef;

	private List<LineItem> lineItem;

	private String successUrl;
	private String cancelUrl;

}
