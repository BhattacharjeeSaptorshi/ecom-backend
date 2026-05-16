package com.ecom.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long productId;

	@NotBlank
	@Size(min = 3, message = "Product name must contains at least 3 characters")
	private String productName;
	private String image;

	@NotBlank
	@Size(min = 6, message = "Product name must contains at least 6 characters")
	private String description;
	private Integer quantity;
	private double price;
	private double discount;
	private double specialPrice;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
}
