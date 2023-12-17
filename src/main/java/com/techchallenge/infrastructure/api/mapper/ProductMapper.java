package com.techchallenge.infrastructure.api.mapper;

import com.techchallenge.domain.entity.Product;
import com.techchallenge.infrastructure.api.request.InsertProductRequest;
import com.techchallenge.infrastructure.api.request.ProductResponse;
import com.techchallenge.infrastructure.api.request.UpdateProductRequest;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

	public Product toProduct(UpdateProductRequest request) {
		return new Product(request.getSku(), request.getTitle(), request.getCategory(),
				request.getDescription(), request.getPrice(), request.getImage());
	}
	public Product toProduct(InsertProductRequest request) {
		return new Product(new ObjectId().toString(), request.getTitle(), request.getCategory(),
				request.getDescription(), request.getPrice(), request.getImage());
	}
	
	public ProductResponse toProductResponse(Product product) {
		return new ProductResponse(product.getSku(), product.getTitle(), product.getCategory().toString(),
				product.getDescription(), product.getPrice(), product.getImage());
	}

	public List<ProductResponse> toProductResponseList(List<Product> product) {
		return product.stream().map( this::toProductResponse).toList();
	}
}
