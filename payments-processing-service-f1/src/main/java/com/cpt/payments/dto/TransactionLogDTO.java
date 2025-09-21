package com.cpt.payments.dto;

import lombok.Data;

@Data
public class TransactionLogDTO {

	private int transactionId;
	private String txnFromStatus;
	private String txnToStatus;

}
