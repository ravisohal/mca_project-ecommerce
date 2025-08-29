package com.sohal.mca.project.ecommerce_backend.controller;

import com.sohal.mca.project.ecommerce_backend.model.Product;
import com.sohal.mca.project.ecommerce_backend.service.ProductService;
import com.sohal.mca.project.ecommerce_backend.util.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: This controller handles product-related operations in the
 * e-commerce backend.
 * It provides endpoints for creating, updating, deleting, and retrieving
 * products.
 * The controller uses Swagger annotations for API documentation and includes
 * security annotations...
 */

@RestController
@RequestMapping(ApiConstants.API_V1_BASE_PATH + "/products")
@Tag(name = "Products", description = "Operations related to products in the e-commerce store")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
        logger.info("ProductController initialized.");
    }

    @Operation(summary = "Get all products with pagination and optional filtering", description = "Retrieve a paginated list of all available products. Can be filtered by category name or product name via query parameters.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of products", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<Product>> getAllProducts(
            @Parameter(description = "Page number (0-indexed)") @PageableDefault(page = 0, size = 10) Pageable pageable,
            @Parameter(description = "Name of the category to filter products (e.g., Automotive)") @RequestParam(required = false) String category,
            @Parameter(description = "Keyword to search for in product names (case-insensitive)") @RequestParam(required = false) String name) {

        logger.debug("Received request for products with pagination and filters. Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Product> products = productService.getPagedProducts(pageable, category, name);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get product by ID", description = "Retrieve a single product by its unique ID.")
    @ApiResponse(responseCode = "200", description = "Product found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "ID of the product to retrieve") @PathVariable Long id) {
        logger.debug("Received request to get product by ID: {}", id);
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Search products by name", description = "Search for products whose names contain the specified keyword (case-insensitive).")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products matching the search criteria", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @GetMapping("/search")
    @PreAuthorize("permitAll()") // Anyone can search products
    public ResponseEntity<List<Product>> searchProductsByName(
            @Parameter(description = "Keyword to search for in product names") @RequestParam String name) {
        logger.debug("Received request to search products by name: {}", name);
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Create a new product", description = "Create a new product with details provided in the request body.")
    @ApiResponse(responseCode = "201", description = "Product created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @PostMapping
    @PreAuthorize("isAuthenticated() AND hasRole('ADMIN')") // Only authenticated users with ADMIN role can create
                                                            // products
    public ResponseEntity<Product> createProduct(@RequestBody Product product, @RequestParam Long someLong) {
        logger.debug("Received request to create a new product.");
        Product createdProduct = productService.createProduct(product, someLong);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing product", description = "Update the details of an existing product by its ID.")
    @ApiResponse(responseCode = "200", description = "Product updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() AND hasRole('ADMIN')") // Only authenticated users with ADMIN role can update
                                                            // products
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "ID of the product to update") @PathVariable Long id, @RequestBody Product product,
            @RequestParam Long someLong) {
        logger.debug("Received request to update product with ID: {}", id);
        Product updatedProduct = productService.updateProduct(id, product, someLong);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Delete a product by ID", description = "Delete a product from the database by its unique ID.")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() AND hasRole('ADMIN')") // Only authenticated users with ADMIN role can delete
                                                            // products
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID of the product to delete") @PathVariable Long id) {
        logger.debug("Received request to delete product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get products by category", description = "Retrieve a list of products belonging to a specific category.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products by category", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @GetMapping("/category/{categoryName}")
    @PreAuthorize("permitAll()") // Anyone can view products by category
    public ResponseEntity<List<Product>> getProductsByCategoryName(
            @Parameter(description = "Name of the category to filter products") @PathVariable String categoryName) {
        logger.debug("Received request to get products by category name: {}", categoryName);
        List<Product> products = productService.getProductsByCategoryName(categoryName);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get product low stock", description = "Get list of product have stock below threshold.")
    @ApiResponse(responseCode = "201", description = "Product created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @GetMapping("/low-stock")
    @PreAuthorize("isAuthenticated() AND hasRole('ADMIN')") // Only authenticated users with ADMIN role can view low
                                                            // stock products
    public ResponseEntity<List<Product>> getLowStockProducts(
            @Parameter(description = "Threshold value") @RequestParam int threshold) {
        logger.info("Received request to get low stock products with threshold: {}", threshold);

        List<Product> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }
}