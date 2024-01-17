package com.techchallenge.application.usecases;

import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Product;
import com.techchallenge.infrastructure.api.request.UpdateProductRequest;

import java.util.List;

public interface ProductUseCase {
	
	Product insert(Product product);
	Product update(UpdateProductRequest updateProductRequest);
	Result<List<Product>> findByCategory(String category, int page, int size);
	void delete(String id);
	Product findById(String id);
    List<Product> findByIds(List<String> skus);
}
