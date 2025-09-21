package com.cpt.payments.service.reconcilation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.cpt.payments.constant.ProviderEnum;
import com.cpt.payments.dto.TransactionDTO;
import com.cpt.payments.service.factory.ProviderHandlerFactory;
import com.cpt.payments.service.interfaces.PaymentStatusHandler;
import com.cpt.payments.service.interfaces.ProviderHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReconTransactionAsync {

	@Autowired
	private ProviderHandlerFactory factory;

	@Async
	public void processItem(TransactionDTO txnDto) {
		log.info("Item : " + txnDto);
		
		ProviderHandler providerHandler = factory
				.getProviderHandler(ProviderEnum.getByName(txnDto.getProvider()));
	}
}
