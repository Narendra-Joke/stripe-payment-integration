package com.cpt.payments.utils;

import org.modelmapper.AbstractConverter;

import com.cpt.payments.constant.PaymentMethodEnum;

public class PaymentMethodIdConverter extends AbstractConverter<Integer, String> {

	@Override
	protected String convert(Integer source) {
		return PaymentMethodEnum.getById(source).getName();
	}
}