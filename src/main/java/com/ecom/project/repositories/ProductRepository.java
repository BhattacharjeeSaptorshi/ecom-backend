package com.ecom.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.project.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

}
