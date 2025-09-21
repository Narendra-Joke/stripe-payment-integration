package com.cpt.payments.utils;

import org.modelmapper.AbstractConverter;

import com.cpt.payments.constant.ProviderEnum;

public class ProviderEnumConverter extends AbstractConverter<String, Integer> {
	@Override
	protected Integer convert(String source) {
		return ProviderEnum.getByName(source).getId();
	}
}
