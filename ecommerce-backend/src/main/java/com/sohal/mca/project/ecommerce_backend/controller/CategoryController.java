package com.sohal.mca.project.ecommerce_backend.controller;

import com.sohal.mca.project.ecommerce_backend.model.Category;
import com.sohal.mca.project.ecommerce_backend.repository.CategoryRepository; 
import com.sohal.mca.project.ecommerce_backend.util.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: This controller handles operations related to product categories,
 * including retrieving all categories, getting a category by ID, creating a new category,
 * updating an existing category, and deleting a category. It uses Swagger annotations for API documentation.
 * The controller is annotated with @RestController and @RequestMapping to define the base path for
 * category-related endpoints. The @Tag annotation is used to categorize the controller in the API documentation.
 * The controller uses a logger to log requests and responses for debugging purposes.
 */

@RestController
@RequestMapping(ApiConstants.API_V1_BASE_PATH + "/categories")
@Tag(name = "Categories", description = "Operations related to product categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryRepository categoryRepository; // Injecting repository directly for basic CRUD

    @Autowired
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        logger.info("CategoryController initialized.");
    }

    @Operation(summary = "Get all categories", description = "Retrieve a list of all product categories.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of categories",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)))
    @GetMapping
    @PreAuthorize("permitAll()") // Anyone can view categories
    public ResponseEntity<List<Category>> getAllCategories() {
        logger.debug("Received request to get all categories.");
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Get category by ID", description = "Retrieve a single category by its unique ID.")
    @ApiResponse(responseCode = "200", description = "Category found",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)))
    @ApiResponse(responseCode = "404", description = "Category not found")
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()") // Anyone can view a specific category
    public ResponseEntity<Category> getCategoryById(@Parameter(description = "ID of the category to retrieve") @PathVariable Long id) {
        logger.debug("Received request to get category by ID: {}", id);
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new category", description = "Add a new product category. Requires authentication.")
    @ApiResponse(responseCode = "201", description = "Category created successfully",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)))
    @ApiResponse(responseCode = "400", description = "Invalid category data")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PostMapping
    @PreAuthorize("isAuthenticated()") // Only authenticated users can create categories
    public ResponseEntity<Category> createCategory(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Category object to be created", required = true,
                                              content = @Content(schema = @Schema(implementation = Category.class, example = "{\"name\":\"Electronics\",\"description\":\"Gadgets and electronic devices\"}")))
                                              @RequestBody Category category) {
        logger.info("Received request to create new category: {}", category.getName());
        try {
            Category createdCategory = categoryRepository.save(category);
            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating category: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Update an existing category", description = "Update details of an existing category. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Category updated successfully",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class)))
    @ApiResponse(responseCode = "400", description = "Invalid category data")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can update categories
    public ResponseEntity<Category> updateCategory(@Parameter(description = "ID of the category to update") @PathVariable Long id,
                                              @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated category object", required = true,
                                              content = @Content(schema = @Schema(implementation = Category.class, example = "{\"name\":\"Updated Electronics\",\"description\":\"Updated description for electronic devices\"}")))
                                              @RequestBody Category categoryDetails) {
        logger.info("Received request to update category ID: {}", id);
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(categoryDetails.getName());
                    category.setDescription(categoryDetails.getDescription());
                    Category updatedCategory = categoryRepository.save(category);
                    logger.info("Category with ID {} updated.", id);
                    return ResponseEntity.ok(updatedCategory);
                })
                .orElseGet(() -> {
                    logger.warn("Category with ID {} not found for update.", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Delete a category", description = "Remove a category by its ID. Requires authentication.")
    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can delete categories
    public ResponseEntity<Void> deleteCategory(@Parameter(description = "ID of the category to delete") @PathVariable Long id) {
        logger.info("Received request to delete category ID: {}", id);
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            logger.info("Category with ID {} deleted successfully.", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Category with ID {} not found for deletion.", id);
            return ResponseEntity.notFound().build();
        }
    }
}
