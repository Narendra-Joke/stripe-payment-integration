package com.cpt.payments.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.cpt.payments.constant.ErrorCodeEnum;
import com.cpt.payments.constant.TransactionStatusEnum;
import com.cpt.payments.dto.TransactionDTO;
import com.cpt.payments.exception.ProcessingServiceException;
import com.cpt.payments.service.factory.PaymentStatusFactory;
import com.cpt.payments.service.interfaces.PaymentStatusHandler;
import com.cpt.payments.service.interfaces.PaymentStatusService;

@Component
public class PaymentStatusServiceImpl implements PaymentStatusService {

	@Autowired
	PaymentStatusFactory statusFactory;

	@Override
	public TransactionDTO processStatus(TransactionDTO payment) {
		System.out.println("PaymentServiceImpl.processPaymentStatus()|| " + "payment:" + payment);

		TransactionStatusEnum statusEnum = TransactionStatusEnum.getByName(payment.getTxnStatus());

		System.out.println("statusEnum:" + statusEnum);

		if (statusEnum == null) {
			System.out.println("NULL statusEnum for txnStatus:" + payment.getTxnStatus());
			throw new ProcessingServiceException(ErrorCodeEnum.INVALID_TXN_STATUS.getErrorCode(),
					ErrorCodeEnum.INVALID_TXN_STATUS.getErrorMessage(), HttpStatus.BAD_REQUEST);
			// its client problem 400 BAD REQUEST
		}

		PaymentStatusHandler statusHandler = statusFactory.getStatusHandler(statusEnum);

		if (statusHandler == null) {
			System.out.println("NULL statusHandler for statusEnum:" + statusEnum);
			throw new ProcessingServiceException(ErrorCodeEnum.TXN_STATUS_HANDLER_NOT_CONFIGURED.getErrorCode(),
					ErrorCodeEnum.TXN_STATUS_HANDLER_NOT_CONFIGURED.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		TransactionDTO processStatusResponse = statusHandler.processStatus(payment);

		return processStatusResponse;
	}

}
