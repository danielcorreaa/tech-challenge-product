package com.techchallenge.infrastructure.persistence.repository;

import com.techchallenge.infrastructure.persistence.document.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends MongoRepository<ProductDocument, String> {
	
	Page<ProductDocument> findByCategory(String category, Pageable pageable);
	
	List<ProductDocument> findByIdIn(@Param("ids") List<String> ids);

}
