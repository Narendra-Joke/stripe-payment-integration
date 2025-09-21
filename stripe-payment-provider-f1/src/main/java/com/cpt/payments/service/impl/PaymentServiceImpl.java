package com.cpt.payments.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.cpt.payments.constants.ErrorCodeEnum;
import com.cpt.payments.dto.CreatePaymentReqDTO;
import com.cpt.payments.dto.CreatePaymentResDTO;
import com.cpt.payments.dto.LineItemDTO;
import com.cpt.payments.exception.StripeProviderException;
import com.cpt.payments.service.http.HttpRequest;
import com.cpt.payments.service.http.HttpServiceEngine;
import com.cpt.payments.service.interfaces.PaymentService;
import com.cpt.payments.stripe.Session;
import com.cpt.payments.stripe.StripeErrorWrapper;
import com.google.gson.Gson;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private HttpServiceEngine httpServiceEngine;

	@Autowired
	private Gson gson;

	@Autowired
	private ModelMapper modelMapper;

	@Value("${stripe.createsession.url}")
	private String stripeCreateSessionUrl;

	@Value("${stripe.secretkey}")
	private String stripeSecretKey;

	@Override
	public CreatePaymentResDTO processPayment(CreatePaymentReqDTO paymentReq) {

		System.out.println("PaymentServiceImpl.processPayment() || " + "reqDTO " + paymentReq);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		httpHeaders.setBasicAuth(stripeSecretKey, "");

		MultiValueMap<String, String> formUrlEncodedRequest = new LinkedMultiValueMap<>();
		formUrlEncodedRequest.add("success_url", paymentReq.getSuccessUrl());
		formUrlEncodedRequest.add("cancel_url", paymentReq.getCancelUrl());
		formUrlEncodedRequest.add("mode", "payment");

		
		for (int i = 0; i < paymentReq.getLineItem().size(); i++) {
			LineItemDTO lineItem = paymentReq.getLineItem().get(i);
			formUrlEncodedRequest.add("line_items[" + i + "][price_data][currency]", lineItem.getCurrency());
			formUrlEncodedRequest.add("line_items[" + i + "][price_data][product_data][name]",
					lineItem.getProductName());

			int unitAmount = Integer.valueOf((int) (lineItem.getUnitAmount() * 100));
			formUrlEncodedRequest.add("line_items[" + i + "][price_data][unit_amount]", String.valueOf(unitAmount));
			formUrlEncodedRequest.add("line_items[" + i + "][quantity]", String.valueOf(lineItem.getQuantity()));
		}

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(stripeCreateSessionUrl);
		httpRequest.setHttpHeaders(httpHeaders);
		httpRequest.setMethod(HttpMethod.POST);
		httpRequest.setRequest(formUrlEncodedRequest);

		ResponseEntity<String> response = httpServiceEngine.makeHttpRequest(httpRequest);

		System.out.println("Response from http service response :" + response.getBody());

		if (response.getStatusCode().is2xxSuccessful()) {

			Session session = gson.fromJson(response.getBody(), Session.class);

//			 SessionDTO sessionDto = modelMapper.map(session, SessionDTO.class);

			if (session.getUrl() != null && !session.getUrl().trim().isEmpty()) {
				CreatePaymentResDTO responseObj = modelMapper.map(session, CreatePaymentResDTO.class);

				System.out.println("createPaymentRes : " + responseObj);
				return responseObj;
			}

			System.out.println("Failed to get URL");

			throw new StripeProviderException(
					ErrorCodeEnum.INVALID_SESSION_URL.getErrorCode(),
					ErrorCodeEnum.INVALID_SESSION_URL.getErrorMessage(), 
					HttpStatus.BAD_GATEWAY);
		}

		if (HttpStatus.GATEWAY_TIMEOUT == response.getStatusCode()) {
			throw new StripeProviderException(
					ErrorCodeEnum.UNABLE_TO_CONNECT_WITH_STRIPE.getErrorCode(),
					ErrorCodeEnum.UNABLE_TO_CONNECT_WITH_STRIPE.getErrorMessage(), 
					HttpStatus.GATEWAY_TIMEOUT);
		}

		if (HttpStatus.INTERNAL_SERVER_ERROR == response.getStatusCode()) {
			throw new StripeProviderException(
					ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),
					ErrorCodeEnum.GENERIC_ERROR.getErrorMessage(), 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		StripeErrorWrapper errorResponse = gson.fromJson(response.getBody(), StripeErrorWrapper.class);

		System.out.println("Got error response from stripe | " + "errorResponse :" + errorResponse);

		throw new StripeProviderException(errorResponse.getError().getType(), errorResponse.getError().getMessage(),
				HttpStatus.valueOf(response.getStatusCode().value()));
	}
}
