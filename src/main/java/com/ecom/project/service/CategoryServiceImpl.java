package com.ecom.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecom.project.entity.Category;
import com.ecom.project.exceptions.APIException;
import com.ecom.project.exceptions.ResourceNotFoundException;
import com.ecom.project.payload.CategoryDTO;
import com.ecom.project.payload.CategoryResponse;
import com.ecom.project.repositories.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService{
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
	
		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
		
		//List<Category> categories = categoryRepository.findAll();
		List<Category> categories = categoryPage.getContent();
		
		if(categoryRepository.count() == 0)
			throw new APIException("Category list is empty !!");
		
		List<CategoryDTO> categoryDTOs = categories.stream()
				.map(category -> modelMapper.map(category, CategoryDTO.class))
				.toList();
		
		CategoryResponse categoryResponse = new CategoryResponse();
		categoryResponse.setContent(categoryDTOs);
		categoryResponse.setPageNumber(categoryPage.getNumber());
		categoryResponse.setPageSize(categoryPage.getSize());
		categoryResponse.setTotalElements(categoryPage.getTotalElements());
		categoryResponse.setTotalPages(categoryPage.getTotalPages());
		categoryResponse.setLastPage(categoryPage.isLast());
				
		return categoryResponse;		
	}
	
	@Override
	public CategoryDTO getCategory(Long categoryId) {
		
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category","category id",categoryId));
							
		return modelMapper.map(category, CategoryDTO.class);
	}

	@Override
	public CategoryDTO createCategory(CategoryDTO categoryDTO) {
		
		Category category = modelMapper.map(categoryDTO, Category.class);
		
		Category categoryfromDB = categoryRepository.findByCategoryName(category.getCategoryName());
		if(categoryfromDB != null)
			throw new APIException("Category with category name "+ category.getCategoryName() +" already exists !!!");
		
		Category savedCategory = categoryRepository.save(category);
		CategoryDTO savedCategoryDTO = modelMapper.map(savedCategory, CategoryDTO.class);

		return savedCategoryDTO;
	}

	@Override
	public CategoryDTO deleteCategory(Long categoryId) {
				
		Category existingCategory = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category","category id",categoryId));
		
		categoryRepository.delete(existingCategory);
		
		CategoryDTO categoryDTO = modelMapper.map(existingCategory, CategoryDTO.class);
		
		//return("Category with categoryId "+ categoryId +" deleted successfully");
		return categoryDTO;
	}

	@Override
	public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
		
		Category category = modelMapper.map(categoryDTO, Category.class);
		
		Category existingCategory = categoryRepository.findById(categoryId)
								.orElseThrow(() -> new ResourceNotFoundException("Category","category id",categoryId));
		
		existingCategory.setCategoryName(category.getCategoryName());
		Category savedCategory = categoryRepository.save(existingCategory);
		
		CategoryDTO categoryDTO2 = modelMapper.map(savedCategory, CategoryDTO.class);
		
		return categoryDTO2;		
	}
}
