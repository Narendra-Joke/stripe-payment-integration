package com.cpt.payments.service.interfaces;

import com.cpt.payments.dto.InitiatePaymentReqDTO;
import com.cpt.payments.dto.InitiatePaymentResDTO;

public interface PaymentService {

	public InitiatePaymentResDTO initiatePayment(InitiatePaymentReqDTO req);

}
