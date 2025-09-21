package com.cpt.payments.stripeprovider;

import lombok.Data;

@Data
public class ErrorResponse {
	private String errorCode;
	private String errorMessage;
}
