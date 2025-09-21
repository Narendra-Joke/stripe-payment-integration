package com.cpt.payments.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpt.payments.constant.ProviderEnum;
import com.cpt.payments.dao.interfaces.TransactionDao;
import com.cpt.payments.dto.InitiatePaymentReqDTO;
import com.cpt.payments.dto.InitiatePaymentResDTO;
import com.cpt.payments.dto.TransactionDTO;
import com.cpt.payments.service.factory.ProviderHandlerFactory;
import com.cpt.payments.service.interfaces.PaymentService;
import com.cpt.payments.service.interfaces.ProviderHandler;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private ProviderHandlerFactory providerFactory;

	@Autowired
	private TransactionDao txnDao;

	@Override
	public InitiatePaymentResDTO initiatePayment(InitiatePaymentReqDTO req) {

		System.out.println("PaymentServiceImpl.initiatePayment()" + "|req:" + req);

		TransactionDTO dto = txnDao.getTransactionById(req.getId());

		ProviderEnum providerEnum = ProviderEnum.getByName(dto.getProvider());
		ProviderHandler providerHandler = providerFactory.getProviderHandler(providerEnum);

		InitiatePaymentResDTO providerResponse = providerHandler.processPayment(dto, req);

		System.out.println("Got response from provider| providerResponse:" + providerResponse);

		return providerResponse;
	}

}
