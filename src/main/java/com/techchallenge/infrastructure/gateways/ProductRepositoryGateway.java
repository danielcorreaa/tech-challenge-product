package com.techchallenge.infrastructure.gateways;


import com.techchallenge.application.gateway.ProductGateway;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Product;
import com.techchallenge.infrastructure.persistence.document.ProductDocument;
import com.techchallenge.infrastructure.persistence.mapper.ProductEntityMapper;
import com.techchallenge.infrastructure.persistence.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class ProductRepositoryGateway implements ProductGateway {

	private ProductRepository repository;

	private ProductEntityMapper mapper;

	public ProductRepositoryGateway(ProductRepository repository, ProductEntityMapper mapper) {
		super();
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public Product insert(Product product) {
		ProductDocument productDocument = repository.save(mapper.toProductDocument(product));
		return mapper.toProduct(productDocument);
	}

	@Override
	public Product update(Product product) {
		return insert(product);
	}

	@Override
	public void delete(String id) {
		repository.deleteById(id);
	}

	@Override
	public Result<List<Product>> findByCategory(String category, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<ProductDocument> pageProductDocuments = repository.findByCategory(category, pageable);
		List<Product> list = mapper.toProductList(pageProductDocuments.stream().toList());
		return Result.ok(list,
				pageProductDocuments.hasNext(),
				pageProductDocuments.getTotalElements());
	}

	@Override
	public Optional<Product> findById(String id) {
		var productEntity =  repository.findById(id);
		return productEntity.map( entity -> mapper.toProduct(entity));
	}

	@Override
	public Optional<List<Product>> findByIds(List<String> productsId) {
		var findByIdIn = repository.findByIdIn(productsId);
		if(findByIdIn.isEmpty()) {
			return Optional.empty();
		}
		List<Product> response = findByIdIn.stream().map(entity -> mapper.toProduct(entity))
				.toList();
		return Optional.of(response);
	}

}
