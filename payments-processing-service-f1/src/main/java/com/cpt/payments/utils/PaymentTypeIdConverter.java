package com.cpt.payments.utils;

import org.modelmapper.AbstractConverter;

import com.cpt.payments.constant.PaymentTypeEnum;

public class PaymentTypeIdConverter extends AbstractConverter<Integer, String> {

	@Override
	protected String convert(Integer source) {
		return PaymentTypeEnum.getById(source).getName();
	}
}
