package com.techchallenge.infrastructure.persistence.mapper;

import com.techchallenge.domain.entity.Product;
import com.techchallenge.infrastructure.persistence.document.ProductDocument;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ProductEntityMapper {

	public ProductDocument toProductDocument(Product product) {
		return new ProductDocument(product.getSku(), product.getTitle(), product.getCategory().toString(),
				product.getDescription(), product.getPrice(), product.getImage());
	}

	public Product toProduct(ProductDocument entity) {
		return new Product(entity.getId(), entity.getTitle(),entity.getCategory(),
				entity.getDescription(), entity.getPrice(), entity.getImage());
	}

	public List<Product> toProductList(List<ProductDocument> entity) {
		return entity.stream().map(this::toProduct).toList();
	}

	public List<ProductDocument> toProductDocumentList(List<Product> product) {
		return product.stream().map(this::toProductDocument).toList();
	}

}
