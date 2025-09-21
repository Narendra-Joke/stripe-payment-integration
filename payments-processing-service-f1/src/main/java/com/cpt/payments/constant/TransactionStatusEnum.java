package com.cpt.payments.constant;

import lombok.Getter;

public enum TransactionStatusEnum {

	CREATED(1, "CREATED"), INITIATED(2, "INITIATED"), PENDING(3, "PENDING"), SUCCESS(4, "SUCCESS"), FAILED(5, "FAILED");

	@Getter
	private final int id;

	@Getter
	private final String name;

	TransactionStatusEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static TransactionStatusEnum getById(int id) {
		for (TransactionStatusEnum status : TransactionStatusEnum.values()) {
			if (status.id == id) {
				return status;
			}
		}

		System.out.println("No TransactionStatusEnum found for id:" + id);
		return null;
	}

	public static TransactionStatusEnum getByName(String name) {
		for (TransactionStatusEnum method : TransactionStatusEnum.values()) {
			if (method.getName().equalsIgnoreCase(name)) {
				return method;
			}
		}

		System.out.println("Invalid TransactionStatusEnum| name:" + name);
		return null;
	}

	@Override
	public String toString() {
		return name;
	}
}
