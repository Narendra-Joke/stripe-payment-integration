package com.cpt.payments.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ProcessingServiceException extends RuntimeException {

	private static final long serialVersionUID = 5806279225416597028L;

	private final String errorCode;
	private final String errorMessage;

	private final HttpStatus httpStatus;

}
