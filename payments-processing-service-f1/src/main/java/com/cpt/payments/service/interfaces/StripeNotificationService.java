package com.cpt.payments.service.interfaces;

import com.cpt.payments.pojo.stripe.StripeEvent;

public interface StripeNotificationService {

	void processStripeEvent(StripeEvent event);

}
