package com.techchallenge.domain.entity;

import com.techchallenge.domain.enums.Category;

import java.math.BigDecimal;

public class Product {

	private String sku;
	private String title;
	private Category category;
	private String description;
	private BigDecimal price;
	private String image;

	public Product(String sku, String title, String categoryName, String description, BigDecimal price, String image) {
		this.category = Category.getByName(categoryName);
		this.sku = sku;
		this.title = title;		
		this.description = description;
		this.price = price;
		this.image = image;
	}

	public String getSku() {
		return sku;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

}
