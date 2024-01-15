package com.techchallenge.domain.enums;

import java.util.Arrays;

public enum Category {
	LANCHE, ACOMPANHAMENTO, BEBIDA, SOBREMESA;

	public static Category getByName(final String name){
		return Arrays.stream(values()).filter(v -> v.name().equals(name)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Invalid Category!"));
	}
	
}
