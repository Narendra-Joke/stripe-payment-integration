package com.cpt.payments.utils;

import org.modelmapper.AbstractConverter;

import com.cpt.payments.constant.PaymentMethodEnum;

public class PaymentMethodEnumConverter extends AbstractConverter<String, Integer> {
	@Override
	protected Integer convert(String source) {
		return PaymentMethodEnum.getByName(source).getId();
	}
}