package com.techchallenge.infrastructure.persistence.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
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


	public ProductDocument(String title, String category, String description, BigDecimal price, String image) {
		super();		
		this.title = title;
		this.category = category;
		this.description = description;
		this.price = price;
		this.image = image;
	}

}
