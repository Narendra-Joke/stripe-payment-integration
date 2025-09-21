package com.cpt.payments.stripe;

import lombok.Data;

@Data
public class StripeError {
	private String code;
	private String param;
	private String decline_code;
	private String message;
	private String type;
}
