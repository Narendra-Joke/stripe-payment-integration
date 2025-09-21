package com.cpt.payments.entity;

import lombok.Data;

@Data
public class TransactionLogEntity {

	private int transactionId;
	private String txnFromStatus;
	private String txnToStatus;

}
