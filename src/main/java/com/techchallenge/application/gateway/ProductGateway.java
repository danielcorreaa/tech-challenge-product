package com.techchallenge.application.gateway;

import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductGateway {
	
	Product insert(Product product);
	Product update(Product product);
	Result<List<Product>> findByCategory(String category, int page, int size);
	void delete(String id);
	Optional<Product> findById(String id);
	Optional<List<Product>> findByIds(List<String> productsId);


}
