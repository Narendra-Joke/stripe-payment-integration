package com.cpt.payments.utils;

import org.modelmapper.AbstractConverter;

import com.cpt.payments.constant.PaymentTypeEnum;

public class PaymentTypeEnumConverter extends AbstractConverter<String, Integer> {
	@Override
	protected Integer convert(String source) {
		return PaymentTypeEnum.getByName(source).getId();
	}
}
