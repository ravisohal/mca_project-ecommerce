package com.sohal.mca.project.ecommerce_backend.service;

import com.sohal.mca.project.ecommerce_backend.model.Category;
import com.sohal.mca.project.ecommerce_backend.model.Product;
import com.sohal.mca.project.ecommerce_backend.repository.CategoryRepository;
import com.sohal.mca.project.ecommerce_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-27
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: ProductService class for the e-commerce application.
 * Provides methods to manage products, including fetching, searching, saving, and deleting products.
 * This service interacts with the ProductRepository to perform CRUD operations.
 */

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository; // Inject CategoryRepository

    /**
     * Constructor for ProductService.
     * @param productRepository The repository to interact with the product database.
     * @param categoryRepository The repository to interact with the category database.
     */
    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        logger.info("ProductService initialized with ProductRepository and CategoryRepository");
    }

    /**
     * Retrieves all products from the database with pagination.
     * @param pageable Pagination and sorting information.
     * @return A Page of products.
     */
    public Page<Product> getPagedProducts(Pageable pageable, String categoryName, String name) {
        logger.debug("Attempting to retrieve all products with pagination. Page: {}, Size: {}, Category: {}, Name: {}", 
                     pageable.getPageNumber(), pageable.getPageSize(), categoryName, name);
        Page<Product> products = Page.empty(pageable);
        Sort validSort = Sort.unsorted();
        
        for (Sort.Order order : pageable.getSort()) {
            if (!"relevance".equalsIgnoreCase(order.getProperty())) {
                validSort = validSort.and(Sort.by(order.getDirection(), order.getProperty()));
            }
        }

        Pageable validPageable = org.springframework.data.domain.PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            validSort
        );
        
        if (categoryName != null && !categoryName.isEmpty()) {
            Category category = categoryRepository.findByName(categoryName).orElse(null);
            logger.debug("Retrieved category by name {}.", category != null ? categoryName : "<not found>");
            if (category != null) {
                products = productRepository.findByCategory(category, validPageable);
            }
        } else if (name != null && !name.isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCase(name, validPageable);
        } else {
            products = productRepository.findAll(validPageable);
        }

        logger.info("Retrieved {} products for page {}.", products.getNumberOfElements(), pageable.getPageNumber());
        return products;
    }

    /**
     * Retrieves all products from the database.
     * @return A list of all products.
     */
    public List<Product> getAllProducts() {
        logger.debug("Attempting to retrieve all products."); // Debug level log
        List<Product> products = productRepository.findAll();
        logger.info("Retrieved {} products.", products.size()); // Info level log
        return products;
    }

    /**
     * Retrieves a product by its ID.
     * @param id The ID of the product.
     * @return An Optional containing the product if found, or empty if not.
     */
    public Optional<Product> getProductById(Long id) {
        logger.debug("Attempting to retrieve product with ID: {}", id);
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            logger.info("Product with ID {} found: {}", id, product.get().getName());
        } else {
            logger.warn("Product with ID {} not found.", id); // Warn level log
        }
        return product;
    }

    /**
     * Creates a new product.
     * The category for the product must exist or be provided as a new Category object.
     * @param product The product object to save.
     * @param categoryId The ID of the category to associate with the product.
     * @return The saved product with its generated ID.
     * @throws IllegalArgumentException if the category is not found.
     */
    @Transactional
    public Product createProduct(Product product, Long categoryId) {
        logger.debug("Creating new product: {} with category ID: {}", product.getName(), categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    logger.error("Category with ID {} not found for product creation.", categoryId);
                    return new IllegalArgumentException("Category not found.");
                });

        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        logger.info("Product created with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    /**
     * Updates an existing product.
     * @param id The ID of the product to update.
     * @param productDetails The updated product details.
     * @param categoryId The ID of the category to associate with the product (can be null if category not changing).
     * @return The updated product, or null if the product was not found.
     * @throws IllegalArgumentException if the category is not found.
     */
    @Transactional
    public Product updateProduct(Long id, Product productDetails, Long categoryId) {
        logger.debug("Attempting to update product with ID: {}", id);
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setDescription(productDetails.getDescription());
                    product.setPrice(productDetails.getPrice());
                    product.setImageUrl(productDetails.getImageUrl());
                    product.setStockQuantity(productDetails.getStockQuantity());

                    if (categoryId != null) {
                        Category category = categoryRepository.findById(categoryId)
                                .orElseThrow(() -> {
                                    logger.error("Category with ID {} not found for product update.", categoryId);
                                    return new IllegalArgumentException("Category not found.");
                                });
                        product.setCategory(category); // Update the Category object
                    }

                    Product updatedProduct = productRepository.save(product);
                    logger.info("Product with ID {} updated.", id);
                    return updatedProduct;
                })
                .orElseGet(() -> {
                    logger.warn("Product with ID {} not found for update.", id);
                    return null; // Or throw an exception
                });
    }

    /**
     * Deletes a product by its ID.
     * @param id The ID of the product to delete.
     */
    @Transactional
    public void deleteProduct(Long id) {
        logger.debug("Attempting to delete product with ID: {}", id);
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            logger.info("Product with ID {} deleted successfully.", id);
        } else {
            logger.warn("Product with ID {} not found for deletion.", id);
        }
    }

    /**
     * Retrieves products by category name.
     * @param categoryName The name of the category.
     * @return A list of products in the specified category.
     */
    public List<Product> getProductsByCategoryName(String categoryName) {
        logger.debug("Searching for products in category by name: {}", categoryName);

        List<Product> products = List.of(); // Initialize to an empty list

        Category category = categoryRepository.findByName(categoryName).orElse(null);
        if (category != null) { 
            products = productRepository.findByCategory(category); 
        } 

        logger.info("Found {} products in category '{}'.", products.size(), categoryName);
        return products;
    }

    /**
     * Searches for products by name (case-insensitive).
     * @param name The name or part of the name to search for.
     * @return A list of products matching the search criteria.
     */
    public List<Product> searchProductsByName(String name) {
        logger.debug("Searching for products with name containing: {}", name);
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        logger.info("Found {} products matching name search '{}'.", products.size(), name);
        return products;
    }


    /**
     * Retrieves a list of products with a stock quantity less than or equal to a given threshold.
     * @param threshold The stock quantity threshold.
     * @return A list of products with low stock.
     */
    public List<Product> getLowStockProducts(int threshold) {
        logger.debug("Attempting to retrieve products with stock <= {}", threshold);
        List<Product> lowStockProducts = productRepository.findByStockQuantityLessThanEqual(threshold);
        logger.info("Found {} products with low stock.", lowStockProducts.size());
        return lowStockProducts;
    }

}
