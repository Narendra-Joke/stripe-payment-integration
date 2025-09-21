package com.cpt.payments.pojo.stripe;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AsyncPaymentFailedData extends StripeDataObject {

	private String id;
	private String status;
	private String payment_status;
}
