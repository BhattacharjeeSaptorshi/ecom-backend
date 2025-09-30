package com.ecom.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.project.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

	Category findByCategoryName(String categoryName);

}
