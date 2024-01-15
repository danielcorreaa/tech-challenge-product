package com.techchallenge.application.usecases.interactor;


import com.techchallenge.application.gateway.ProductGateway;
import com.techchallenge.application.usecases.ProductUseCase;
import com.techchallenge.core.exceptions.NotFoundException;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Product;
import com.techchallenge.domain.enums.Category;
import com.techchallenge.infrastructure.api.request.UpdateProductRequest;


import java.util.List;
import java.util.Optional;

public class ProductUseCaseInteractor implements ProductUseCase{
	
	private final ProductGateway productGateway;

	public ProductUseCaseInteractor(ProductGateway productGateway) {
		super();
		this.productGateway = productGateway;
	}
	
	public Product insert(Product product) {
		return productGateway.insert(product);
	}
	
	public Product update(UpdateProductRequest updateProductRequest) {
		Product product = productGateway.findById(updateProductRequest.getSku())
				.orElseThrow(() -> new NotFoundException("Product not found for update!"));
		merge(updateProductRequest, product);
		return productGateway.update(product);
	}
	public Result<List<Product>> findByCategory(String category, int page, int size) {
		Result<List<Product>> products = productGateway.findByCategory(category, page, size);
		if(products.getBody().isEmpty()){
			throw new NotFoundException("Product not found for category: "+category);
		}
		return productGateway.findByCategory(category, page, size);
	}

	public void delete(String id) {
		productGateway.delete(id);						
	}

	public Product findById(String sku) {
		return productGateway.findById(sku).orElseThrow(() -> new NotFoundException("Product not found!"));
	}

	@Override
	public List<Product> findByIds(List<String> skus) {
		return productGateway.findByIds(skus)
				.orElseThrow(() -> new NotFoundException("Products not found for skus: "+skus));
	}

	private static void merge(UpdateProductRequest updateProductRequest, Product product) {
		Optional.ofNullable(updateProductRequest.getPrice()).ifPresent(product::setPrice);
		Optional.ofNullable(updateProductRequest.getDescription()).ifPresent(product::setDescription);
		Optional.ofNullable(updateProductRequest.getTitle()).ifPresent(product::setTitle);
		Optional.ofNullable(updateProductRequest.getCategory())
				.ifPresent( category -> product.setCategory(Category.getByName(category)));
		Optional.ofNullable(updateProductRequest.getImage()).ifPresent(product::setImage);
	}
}
