package com.cpt.payments.constant;

import lombok.Getter;

public enum ProviderEnum {
	STRIPE(1, "STRIPE");

	@Getter
	private int id;

	@Getter
	private String name;

	ProviderEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static ProviderEnum getById(int id) {
		for (ProviderEnum method : ProviderEnum.values()) {
			if (method.getId() == id) {
				return method;
			}
		}

		System.out.println("Invalid ProviderEnum| id:" + id);
		return null;
	}

	public static ProviderEnum getByName(String name) {
		for (ProviderEnum method : ProviderEnum.values()) {
			if (method.getName().equalsIgnoreCase(name)) {
				return method;
			}
		}

		System.out.println("Invalid ProviderEnum| name:" + name);
		return null;
	}
}
