package com.techchallenge.config;

import com.techchallenge.application.gateway.ProductGateway;
import com.techchallenge.application.usecases.ProductUseCase;
import com.techchallenge.application.usecases.interactor.ProductUseCaseInteractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

	@Bean
	public ProductUseCase productUseCase(ProductGateway productGateway) {
		return new ProductUseCaseInteractor(productGateway);
	}
	
}
