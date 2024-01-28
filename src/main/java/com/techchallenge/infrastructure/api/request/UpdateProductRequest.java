package com.techchallenge.infrastructure.api.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

public record UpdateProductRequest(

		@Getter	
		@NotNull(message = "Id is required!")
		String sku,

		@Getter
		String title, 
		
		@Getter
		String category,
		
		@Getter
		String description,
		
		@Getter
		@DecimalMin(value = "0.1", inclusive = false, message = "Price can't be zero")
	    @Digits(integer=3, fraction=2)
		BigDecimal price,	
		
		@Getter		
		String image ) {

}
