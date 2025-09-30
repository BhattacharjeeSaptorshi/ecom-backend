package com.ecom.project.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ecom.project.config.AppConstants;
import com.ecom.project.entity.Category;
import com.ecom.project.payload.CategoryDTO;
import com.ecom.project.payload.CategoryResponse;
import com.ecom.project.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
//	public CategoryController(CategoryService categoryService) {
//		super();
//		this.categoryService = categoryService;
//	}
//	@GetMapping("/public/page")
//	public ResponseEntity<String> testPage(@RequestParam String msg)
//	{
//		return new ResponseEntity<>("The given message is "+msg,HttpStatus.OK);
//	}

	@GetMapping("/public/categories")
	public ResponseEntity<CategoryResponse> getAllCategories(
									@RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false)Integer pageNumber,
									@RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false)Integer pageSize,
									@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy, 
									@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder)
	{
		CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber,pageSize, sortBy, sortOrder);
		return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
	}
	
	@GetMapping("public/categories/{categoryId}")
	public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long categoryId)
	{
		try {
			CategoryDTO categoryDTO = categoryService.getCategory(categoryId);
			return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(e.getStatusCode());
		}
	}
	
	@PostMapping("/public/categories")
	public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categotyDTO)
	{
		CategoryDTO savedCategoryDTO = categoryService.createCategory(categotyDTO);
		return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/admin/categories/{categoryId}")
	public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId)
	{
		CategoryDTO deletedObject = categoryService.deleteCategory(categoryId);
		return new ResponseEntity<>(deletedObject, HttpStatus.OK);
	}
	
	@PutMapping("/public/categories/{categoryId}")
	public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId)
	{
		CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);
		return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);
	}
}