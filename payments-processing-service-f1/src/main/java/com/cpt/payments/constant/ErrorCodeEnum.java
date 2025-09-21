package com.cpt.payments.constant;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

	GENERIC_ERROR("20000", "Unable to process, please try later"),
	INVALID_TXN_STATUS("20001", "Invalid Request. Incorrect 'txnStatus' received."),
	TXN_STATUS_HANDLER_NOT_CONFIGURED("20002", "StatusHandler not configured, Try again later."),
	DUPLICATE_TXN_REF("20003", "Invalid Request. Duplicate TxnReference");

	private String errorCode;
	private String errorMessage;

	ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}
