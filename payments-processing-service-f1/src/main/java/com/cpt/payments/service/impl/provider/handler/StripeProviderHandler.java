package com.cpt.payments.service.impl.provider.handler;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cpt.payments.constant.ErrorCodeEnum;
import com.cpt.payments.constant.TransactionStatusEnum;
import com.cpt.payments.dto.InitiatePaymentReqDTO;
import com.cpt.payments.dto.InitiatePaymentResDTO;
import com.cpt.payments.dto.TransactionDTO;
import com.cpt.payments.exception.ProcessingServiceException;
import com.cpt.payments.service.http.HttpRequest;
import com.cpt.payments.service.http.HttpServiceEngine;
import com.cpt.payments.service.interfaces.PaymentStatusService;
import com.cpt.payments.service.interfaces.ProviderHandler;
import com.cpt.payments.stripeprovider.CreatePaymentReq;
import com.cpt.payments.stripeprovider.CreatePaymentRes;
import com.cpt.payments.stripeprovider.ErrorResponse;
import com.google.gson.Gson;

@Service
public class StripeProviderHandler implements ProviderHandler {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private HttpServiceEngine httpServiceEngine;

	@Autowired
	private Gson gson;

	@Autowired
	private PaymentStatusService statusService;

	@Value("${stripeprovider.payments.createpayment}")
	private String stripeCreatePaymentUrl;

	@Override
	public InitiatePaymentResDTO processPayment(TransactionDTO txn, InitiatePaymentReqDTO req) {

		System.out.println("StripeProviderHandler.processPayment()" + "|txn:" + txn + "|req:" + req);

		HttpRequest httpRequest = prepareHttpReq(txn, req);

		txn.setTxnStatus(TransactionStatusEnum.INITIATED.getName());
		statusService.processStatus(txn);
		System.out.println("Updated status as txnStatus:" + txn.getTxnStatus());

		ResponseEntity<String> response = httpServiceEngine.makeHttpRequest(httpRequest);

		System.out.println("RESPONSE from http service ||response:" + response.getBody());

		InitiatePaymentResDTO dtoAfterResponseProcessing = processResponse(txn, response);

		System.out.println("SuccessFully processed response| dtoAfterResponseProcessing:" + dtoAfterResponseProcessing);

		txn.setProviderReference(dtoAfterResponseProcessing.getId());

		txn.setTxnStatus(TransactionStatusEnum.PENDING.getName());
		statusService.processStatus(txn);

		System.out.println("Updated status as txnStatus:" + txn.getTxnStatus());

		return dtoAfterResponseProcessing;
	}
	
	private InitiatePaymentResDTO processResponse(TransactionDTO txn, ResponseEntity<String> response) {
		if (HttpStatus.CREATED == response.getStatusCode()) {
			CreatePaymentRes responseAsObj = gson.fromJson(response.getBody(), CreatePaymentRes.class);

			InitiatePaymentResDTO resDto = modelMapper.map(responseAsObj, InitiatePaymentResDTO.class);
			resDto.setTxnReference(txn.getTxnReference());

			System.out.println("resDto:" + resDto);

			return resDto;
		}

		if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {

			System.out.println("Received 4xx or 5xx error: StatusCode:" + response.getStatusCode());
			ErrorResponse errorResponseFromStripeProvider = gson.fromJson(response.getBody(), ErrorResponse.class);

			throw new ProcessingServiceException(errorResponseFromStripeProvider.getErrorCode(),
					errorResponseFromStripeProvider.getErrorMessage(),
					HttpStatus.valueOf(response.getStatusCode().value()));
		}

		System.out.println("Got exception processing HTTP call to provider service");
		throw new ProcessingServiceException(ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),
				ErrorCodeEnum.GENERIC_ERROR.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private HttpRequest prepareHttpReq(TransactionDTO txn, InitiatePaymentReqDTO req) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		CreatePaymentReq paymentRequest = modelMapper.map(req, CreatePaymentReq.class);
		paymentRequest.setTxnRef(txn.getTxnReference());

		System.out.println("Request for StripeProvider" + "|paymentRequest:" + paymentRequest);

		// Prepare HttpRequest for passing to HttpServiceEngine
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(stripeCreatePaymentUrl);
		httpRequest.setMethod(HttpMethod.POST);
		httpRequest.setRequest(paymentRequest);
		httpRequest.setHttpHeaders(httpHeaders);
		return httpRequest;
	}

}
