package com.cpt.payments.dto;

import java.util.List;

import com.cpt.payments.pojo.LineItem;

import lombok.Builder;
import lombok.Data;

@Data
public class CreatePaymentReqDTO {
	private String txnRef;
	private List<LineItemDTO> lineItem;
	private String successUrl;
	private String cancelUrl;
}