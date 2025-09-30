package com.ecom.project.service;

import java.util.List;

import com.ecom.project.entity.Category;
import com.ecom.project.payload.CategoryDTO;
import com.ecom.project.payload.CategoryResponse;

public interface CategoryService {

	//List<Category> getAllCategories();
	CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	//void createCategory(Category category);
	CategoryDTO createCategory(CategoryDTO categoryDTO);
	//String deleteCategory(Long categoryId);
	CategoryDTO deleteCategory(Long categoryId);
	//Category updateCategory(Category category, Long categoryId);
	CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
	//Category getCategory(Long categoryId);
	CategoryDTO getCategory(Long categoryId);
}
