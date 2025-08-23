package com.sohal.mca.project.ecommerce_backend.repository;

import com.sohal.mca.project.ecommerce_backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: CategoryRepository interface for the e-commerce application.
 * Provides methods to perform CRUD operations on Category entities.
 * This interface extends JpaRepository to leverage Spring Data JPA features.
 * It allows for easy interaction with the database without the need for boilerplate code.
 * The Category entity represents product categories in the e-commerce system.
 * It includes methods for saving, deleting, and finding categories.
 */

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);
    
    List<Category> findByNameContaining(String keyword);

    List<Category> findByDescriptionContaining(String keyword);
}
