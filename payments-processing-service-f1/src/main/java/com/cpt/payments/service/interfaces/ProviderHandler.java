package com.cpt.payments.service.interfaces;

import com.cpt.payments.dto.InitiatePaymentResDTO;
import com.cpt.payments.dto.InitiatePaymentReqDTO;
import com.cpt.payments.dto.TransactionDTO;

public interface ProviderHandler {

	public InitiatePaymentResDTO processPayment(TransactionDTO txn, InitiatePaymentReqDTO req);

}
