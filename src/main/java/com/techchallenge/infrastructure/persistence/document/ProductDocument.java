package com.techchallenge.infrastructure.persistence.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;


@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Document(collection = "product")
public class ProductDocument {

	@Id
	private String id;
	private String title;
	private String category;
	private String description;
	private BigDecimal price;
	private String image;


}
