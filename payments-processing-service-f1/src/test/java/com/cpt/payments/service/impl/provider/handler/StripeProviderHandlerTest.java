package com.cpt.payments.service.impl.provider.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.cpt.payments.constant.ErrorCodeEnum;
import com.cpt.payments.dto.InitiatePaymentReqDTO;
import com.cpt.payments.dto.InitiatePaymentResDTO;
import com.cpt.payments.dto.LineItemDTO;
import com.cpt.payments.dto.TransactionDTO;
import com.cpt.payments.exception.ProcessingServiceException;
import com.cpt.payments.service.http.HttpRequest;
import com.cpt.payments.service.http.HttpServiceEngine;
import com.cpt.payments.service.interfaces.PaymentStatusService;
import com.cpt.payments.stripeprovider.CreatePaymentReq;
import com.cpt.payments.stripeprovider.CreatePaymentRes;
import com.cpt.payments.stripeprovider.ErrorResponse;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class StripeProviderHandlerTest {

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private PaymentStatusService statusService;

	@Mock
	private HttpServiceEngine httpServiceEngine;

	@Mock
	private Gson gson;

	@InjectMocks
	private StripeProviderHandler stripeProviderHandler;

//	private TransactionDTO txn;
//	private CreatePaymentReq createPaymentReq;
//	
//	@BeforeEach
//	void setUp() {
//		txn = new TransactionDTO();
//		txn.setTxnReference("TXN-TEST-001");
//		
//		createPaymentReq = new CreatePaymentReq();
//		createPaymentReq.setSuccessUrl("https://success.com");
//	}

	@Test
	public void testMethod() {
		log.info("running testMethod");

		// Arrange
		TransactionDTO txn = new TransactionDTO();
		txn.setTxnReference("TXN-TEST-001");

		List<LineItemDTO> lineItemList = new ArrayList<>();

		LineItemDTO lineItemDto = LineItemDTO.builder().currency("USD").productName("IPHONE").quantity(1)
				.unitAmount(100000.00).build();
		lineItemList.add(lineItemDto);

		InitiatePaymentReqDTO req = InitiatePaymentReqDTO.builder().id(1).lineItem(lineItemList).successUrl("https://")
				.cancelUrl("https://").build();

		CreatePaymentReq createPaymentReq = new CreatePaymentReq();
		createPaymentReq.setSuccessUrl("https://success.com");

		when(modelMapper.map(any(InitiatePaymentReqDTO.class), eq(CreatePaymentReq.class)))
				.thenReturn(createPaymentReq);

		ReflectionTestUtils.setField(stripeProviderHandler, "stripeCreatePaymentUrl",
				"http://localhost:8086/v1/payments");

		ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.CREATED);
		when(httpServiceEngine.makeHttpRequest(any(HttpRequest.class))).thenReturn(responseEntity);

		String jsonResponse = "";
		CreatePaymentRes createPaymentRes = new CreatePaymentRes();
		createPaymentRes.setId("paymentId123");
		createPaymentRes.setUrl("https://");

		when(gson.fromJson(eq(jsonResponse), eq(CreatePaymentRes.class))).thenReturn(createPaymentRes);

		InitiatePaymentResDTO resDTO = new InitiatePaymentResDTO();
		resDTO.setId("123");
		resDTO.setTxnReference("TXN-TEST-001");

		when(modelMapper.map(any(CreatePaymentRes.class), eq(InitiatePaymentResDTO.class))).thenReturn(resDTO);

		// Act
		InitiatePaymentResDTO result = stripeProviderHandler.processPayment(txn, req);

		// Assert
		assertNotNull(result);
		assertEquals("123", result.getId());
		verify(statusService, times(2)).processStatus(txn);

		
	}

//	@Test
//	void test4xxClientErrorResponse() {
//		// Arrange
//		ResponseEntity<String> responseEntity = new ResponseEntity<>("error response body", HttpStatus.BAD_REQUEST);
//		ErrorResponse errorResponse = new ErrorResponse();
//		errorResponse.setErrorCode("ERR400");
//		errorResponse.setErrorMessage("Client Error Occurred");
//
//		when(gson.fromJson(anyString(), eq(ErrorResponse.class))).thenReturn(errorResponse);
//
//		// Act
//		ProcessingServiceException exception = assertThrows(ProcessingServiceException.class, () -> {
//			stripeProviderHandler.processResponse(new TransactionDTO(), responseEntity);
//		});
//
//		// Assert
//		assertEquals("ERR400", exception.getErrorCode());
//		assertEquals("Client Error Occurred", exception.getErrorMessage());
//		assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
//	}
//
//	@Test
//	void test5xxServerErrorResponse() {
//		// Arrange
//		ResponseEntity<String> responseEntity = new ResponseEntity<>("error response body",
//				HttpStatus.INTERNAL_SERVER_ERROR);
//		ErrorResponse errorResponse = new ErrorResponse();
//		errorResponse.setErrorCode("ERR500");
//		errorResponse.setErrorMessage("Server Error Occurred");
//
//		when(gson.fromJson(anyString(), eq(ErrorResponse.class))).thenReturn(errorResponse);
//
//		// Act
//		ProcessingServiceException exception = assertThrows(ProcessingServiceException.class, () -> {
//			stripeProviderHandler.processResponse(new TransactionDTO(), responseEntity);
//		});
//
//		// Assert exception details
//		assertEquals("ERR500", exception.getErrorCode());
//		assertEquals("Server Error Occurred", exception.getErrorMessage());
//		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
//	}
//
//	@Test
//	void testGenericErrorException() {
//		// Arrange
//		ResponseEntity<String> responseEntity = new ResponseEntity<>("unexpected response body",
//				HttpStatus.NO_CONTENT);
//
//		// Act
//		ProcessingServiceException exception = assertThrows(ProcessingServiceException.class, () -> {
//			stripeProviderHandler.processResponse(new TransactionDTO(), responseEntity);
//		});
//
//		// Assert exception details
//		assertEquals(ErrorCodeEnum.GENERIC_ERROR.getErrorCode(), exception.getErrorCode());
//		assertEquals(ErrorCodeEnum.GENERIC_ERROR.getErrorMessage(), exception.getErrorMessage());
//		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
//
//	}

}
