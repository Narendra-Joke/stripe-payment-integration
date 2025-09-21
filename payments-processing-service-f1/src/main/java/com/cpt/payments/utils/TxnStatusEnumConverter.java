package com.cpt.payments.utils;

import org.modelmapper.AbstractConverter;

import com.cpt.payments.constant.TransactionStatusEnum;

public class TxnStatusEnumConverter extends AbstractConverter<String, Integer> {
	@Override
	protected Integer convert(String source) {
		return TransactionStatusEnum.getByName(source).getId();
	}
}
