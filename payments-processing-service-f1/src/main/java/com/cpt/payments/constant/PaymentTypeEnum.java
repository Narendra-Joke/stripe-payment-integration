package com.cpt.payments.constant;

import lombok.Getter;

public enum PaymentTypeEnum {
	SALE(1, "SALE");

	@Getter
	private int id;

	@Getter
	private String name;

	PaymentTypeEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static PaymentTypeEnum getById(int id) {
		for (PaymentTypeEnum method : PaymentTypeEnum.values()) {
			if (method.getId() == id) {
				return method;
			}
		}

		System.out.println("Invalid ProviderEnum| id:" + id);
		return null;
	}

	public static PaymentTypeEnum getByName(String name) {
		for (PaymentTypeEnum method : PaymentTypeEnum.values()) {
			if (method.getName().equalsIgnoreCase(name)) {
				return method;
			}
		}

		System.out.println("Invalid ProviderEnum| name:" + name);
		return null;
	}
}
