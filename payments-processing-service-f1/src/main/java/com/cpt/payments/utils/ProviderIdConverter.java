package com.cpt.payments.utils;

import org.modelmapper.AbstractConverter;

import com.cpt.payments.constant.ProviderEnum;

public class ProviderIdConverter extends AbstractConverter<Integer, String> {
	@Override
	protected String convert(Integer source) {
		return ProviderEnum.getById(source).getName();
	}
}
