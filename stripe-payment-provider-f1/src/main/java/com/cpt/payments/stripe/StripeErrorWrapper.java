package com.cpt.payments.stripe;

import lombok.Data;

@Data
public class StripeErrorWrapper {
	private StripeError error;
}
