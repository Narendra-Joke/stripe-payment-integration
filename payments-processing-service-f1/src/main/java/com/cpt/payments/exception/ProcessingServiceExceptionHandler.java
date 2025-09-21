package com.cpt.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.cpt.payments.constant.ErrorCodeEnum;
import com.cpt.payments.pojo.ErrorResponse;

@ControllerAdvice
public class ProcessingServiceExceptionHandler {

	@ExceptionHandler(ProcessingServiceException.class)
	public ResponseEntity<ErrorResponse> handleProcessingException(ProcessingServiceException ex) {

		System.out.println("Received ex:" + ex);

		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(ex.getErrorCode());
		errorResponse.setErrorMessage(ex.getErrorMessage());

		System.out.println("returning errorResponse:" + errorResponse);
		return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

		System.out.println("Received ex:" + ex);

		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(ErrorCodeEnum.GENERIC_ERROR.getErrorCode());
		errorResponse.setErrorMessage(ErrorCodeEnum.GENERIC_ERROR.getErrorMessage());

		System.out.println("returning errorResponse:" + errorResponse);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
