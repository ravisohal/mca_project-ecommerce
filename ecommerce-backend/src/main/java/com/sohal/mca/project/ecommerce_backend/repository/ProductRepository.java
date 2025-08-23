package com.sohal.mca.project.ecommerce_backend.repository;

import com.sohal.mca.project.ecommerce_backend.model.Category;
import com.sohal.mca.project.ecommerce_backend.model.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: ProductRepository interface for the e-commerce application.
 * Provides methods to perform CRUD operations on Product entities.
 * This interface can be extended to include custom query methods as needed.
 */

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(Category category);
    
    Page<Product> findByCategory(Category category, Pageable pageable);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    
    List<Product> findByCategoryIdAndPriceBetween(Category category, Double minPrice, Double maxPrice);

    List<Product> findByCategoryIdAndNameContainingIgnoreCase(Category category, String name);
    
    List<Product> findByNameContainingIgnoreCaseAndPriceBetween(String name, Double minPrice, Double maxPrice);

    List<Product> findByCategoryIdAndNameContainingIgnoreCaseAndPriceBetween(Category category, String name, Double minPrice, Double maxPrice);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable validPageable);

}
