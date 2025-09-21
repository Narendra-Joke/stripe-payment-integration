package com.cpt.payments.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpt.payments.constant.TransactionStatusEnum;
import com.cpt.payments.dao.interfaces.TransactionDao;
import com.cpt.payments.dto.TransactionDTO;
import com.cpt.payments.pojo.stripe.AsyncPaymentFailedData;
import com.cpt.payments.pojo.stripe.AsyncPaymentSucceededData;
import com.cpt.payments.pojo.stripe.SessionCompletedData;
import com.cpt.payments.pojo.stripe.StripeEvent;
import com.cpt.payments.service.interfaces.PaymentStatusService;
import com.cpt.payments.service.interfaces.StripeNotificationService;
import com.google.gson.Gson;

@Service
public class StripeNotificationServiceImpl implements StripeNotificationService {

	private static final String PAYMENT_STATUS_PAID = "paid";
	private static final String STATUS_COMPLETE = "complete";
	private static final String CHECKOUT_SESSION_COMPLETED = "checkout.session.completed";
	private static final String CHECKOUT_SESSION_ASYNC_PAYMENT_SUCCEEDED = "checkout.session.async_payment_succeeded";
	private static final String CHECKOUT_SESSION_ASYNC_PAYMENT_FAILED = "checkout.session.async_payment_failed";

	@Autowired
	private Gson gson;

	@Autowired
	private PaymentStatusService paymentStatusServiceImpl;

	@Autowired
	private TransactionDao transactionDaoImpl;

	@Override
	public void processStripeEvent(StripeEvent event) {
		System.out.println("event:" + event);

		if (CHECKOUT_SESSION_ASYNC_PAYMENT_FAILED.equals(event.getType())) {
			System.out.println("Update failed processing");

			AsyncPaymentFailedData eventData = gson.fromJson(event.getData().getObject(), AsyncPaymentFailedData.class);

			System.out.println(CHECKOUT_SESSION_ASYNC_PAYMENT_FAILED + " received");
			System.out.println("eventData:" + eventData);

			TransactionDTO transaction = transactionDaoImpl.getTransactionByProviderReference(eventData.getId());

			transaction.setTxnStatus(TransactionStatusEnum.FAILED.getName());
			paymentStatusServiceImpl.processStatus(transaction);

			System.out.println("Updated txn as FAILED");
			return;
		}

		if (CHECKOUT_SESSION_ASYNC_PAYMENT_SUCCEEDED.equals(event.getType())) {
			System.out.println("Update Success processing");

			AsyncPaymentSucceededData eventData = gson.fromJson(event.getData().getObject(),
					AsyncPaymentSucceededData.class);

			System.out.println(CHECKOUT_SESSION_ASYNC_PAYMENT_SUCCEEDED + " received");
			System.out.println("eventData:" + eventData);

			TransactionDTO transaction = transactionDaoImpl.getTransactionByProviderReference(eventData.getId());

			transaction.setTxnStatus(TransactionStatusEnum.SUCCESS.getName());
			paymentStatusServiceImpl.processStatus(transaction);

			System.out.println("Updated txn as SUCCESS");

			return;
		}

		if (CHECKOUT_SESSION_COMPLETED.equals(event.getType())) {
			SessionCompletedData stripeDataObj = gson.fromJson(event.getData().getObject(), SessionCompletedData.class);

			System.out.println("checkout.session.completed received");
			System.out.println("stripeDataObj:" + stripeDataObj);

			if (stripeDataObj.getStatus().equals(STATUS_COMPLETE)
					&& stripeDataObj.getPayment_status().equals(PAYMENT_STATUS_PAID)) {
				System.out.println("--- SUCCESSFULLY PROCESSED ---");

				TransactionDTO transaction = transactionDaoImpl
						.getTransactionByProviderReference(stripeDataObj.getId());

				transaction.setTxnStatus(TransactionStatusEnum.SUCCESS.getName());
				paymentStatusServiceImpl.processStatus(transaction);

				System.out.println("Updated txn as SUCCESS");
			}

			return;
		}

		System.out.println("Received eventType:" + event.getType());

	}

}
