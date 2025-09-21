package com.cpt.payments.service.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.cpt.payments.constant.TransactionStatusEnum;
import com.cpt.payments.service.impl.handler.CreatedStatusHandler;
import com.cpt.payments.service.impl.handler.FailedStatusHandler;
import com.cpt.payments.service.impl.handler.InitiatedStatusHandler;
import com.cpt.payments.service.impl.handler.PendingStatusHandler;
import com.cpt.payments.service.impl.handler.SuccessStatusHandler;
import com.cpt.payments.service.interfaces.PaymentStatusHandler;

@Component
public class PaymentStatusFactory {

	@Autowired
	private ApplicationContext ctx;

	public PaymentStatusHandler getStatusHandler(TransactionStatusEnum statusEnum) {

		switch (statusEnum) {
		case CREATED:
			System.out.println("Creating CreatedStatusHandler for " + "status:" + statusEnum);

			CreatedStatusHandler createdStatusHandler = ctx.getBean(CreatedStatusHandler.class);
			System.out.println("Got bean from APplcationContext ||" + "createdStatusHandler:" + createdStatusHandler);

			return createdStatusHandler;

		case INITIATED:
			return ctx.getBean(InitiatedStatusHandler.class);

		case PENDING:
			return ctx.getBean(PendingStatusHandler.class);

		case FAILED:
			return ctx.getBean(FailedStatusHandler.class);

		case SUCCESS:
			return ctx.getBean(SuccessStatusHandler.class);

		default:
			System.out.println("Unable to find Handler");
			return null;
		}
	}
}
