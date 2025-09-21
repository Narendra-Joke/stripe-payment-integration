package com.cpt.payments.constant;

import lombok.Getter;

public enum PaymentMethodEnum {
	APM(1, "APM");

	@Getter
	private int id;

	@Getter
	private String name;

	PaymentMethodEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static PaymentMethodEnum getById(int id) {
		for (PaymentMethodEnum method : PaymentMethodEnum.values()) {
			if (method.getId() == id) {
				return method;
			}
		}

		System.out.println("Invalid paymentMethodId| id:" + id);
		return null;
	}

	public static PaymentMethodEnum getByName(String name) {
		for (PaymentMethodEnum method : PaymentMethodEnum.values()) {
			if (method.getName().equalsIgnoreCase(name)) {
				return method;
			}
		}

		System.out.println("Invalid paymentMethodName| name:" + name);
		return null;
	}
}
