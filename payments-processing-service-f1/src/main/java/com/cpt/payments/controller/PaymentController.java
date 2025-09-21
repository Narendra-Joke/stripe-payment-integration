package com.cpt.payments.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpt.payments.constant.EndPoints;
import com.cpt.payments.dto.InitiatePaymentReqDTO;
import com.cpt.payments.dto.InitiatePaymentResDTO;
import com.cpt.payments.dto.TransactionDTO;
import com.cpt.payments.pojo.CreateTransactionRes;
import com.cpt.payments.pojo.InitiatePaymentReq;
import com.cpt.payments.pojo.InitiatePaymentRes;
import com.cpt.payments.pojo.Transaction;
import com.cpt.payments.service.interfaces.PaymentService;
import com.cpt.payments.service.interfaces.PaymentStatusService;

@RestController
@RequestMapping(EndPoints.V1_PAYMENTS)
public class PaymentController {

//	@Autowired
	private ModelMapper modelMapper;

//	@Autowired
	private PaymentStatusService statusService;

//	@Autowired
	private PaymentService paymentService;

	public PaymentController(ModelMapper modelMapper, PaymentStatusService service, PaymentService paymentService) {
		this.modelMapper = modelMapper;
		this.statusService = service;
		this.paymentService = paymentService;
	}

	@PostMapping(value = { EndPoints.EMPTY_STRING, EndPoints.SLASH })
	public ResponseEntity<CreateTransactionRes> createPayment(@RequestBody Transaction transaction) {

		System.out.println("****Starting payment processing" + "|transaction:" + transaction);

		// ModelMapper =>
		TransactionDTO txnDTO = modelMapper.map(transaction, TransactionDTO.class);

		System.out.println("Transaction object after conversion" + "|txnDTO:" + txnDTO);

		TransactionDTO returnObject = statusService.processStatus(txnDTO);

		System.out.println("PaymentController.createPayment() returning response:" + "|valFromService:" + returnObject);

		CreateTransactionRes response = new CreateTransactionRes();
		response.setId(returnObject.getId());
		response.setTxnStatus(returnObject.getTxnStatus());

		System.out.println("Returning response:" + response);

		ResponseEntity<CreateTransactionRes> responseEntity = new ResponseEntity<>(response, HttpStatus.CREATED);

		System.out.println("responseEntity:" + responseEntity);

		return responseEntity;
	}

	@PostMapping(EndPoints.PAYMENT_INITIATE)
	public ResponseEntity<InitiatePaymentRes> initiatePayment(@PathVariable int id,
			@RequestBody InitiatePaymentReq paymentReq) {
		System.out.println("PaymentController.initiatePayment()||" + "|id:" + id + "|paymentReq:" + paymentReq);

		InitiatePaymentReqDTO reqDTO = modelMapper.map(paymentReq, InitiatePaymentReqDTO.class);
		reqDTO.setId(id);

		System.out.println("Converted to DTO|reqDTO:" + reqDTO);

		InitiatePaymentResDTO serviceResponse = paymentService.initiatePayment(reqDTO);

		InitiatePaymentRes res = modelMapper.map(serviceResponse, InitiatePaymentRes.class);

		System.out.println("Got response service" + "|serviceResponse:" + serviceResponse + "|endpoint res:" + res);

		return new ResponseEntity<>(res, HttpStatus.OK);
	}

}
