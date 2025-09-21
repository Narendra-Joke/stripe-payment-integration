package com.cpt.payments.utils;

import org.modelmapper.AbstractConverter;

import com.cpt.payments.constant.TransactionStatusEnum;

public class TxnStatusIdConverter extends AbstractConverter<Integer, String> {

	@Override
	protected String convert(Integer source) {
		return TransactionStatusEnum.getById(source).getName();
	}
}
