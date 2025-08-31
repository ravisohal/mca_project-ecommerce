package com.sohal.mca.project.ecommerce_backend.service;

import com.sohal.mca.project.ecommerce_backend.model.Product;
import com.sohal.mca.project.ecommerce_backend.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

/** 
 * Author: Ravi Sohal
 * Date: 2025-08-15
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: This service handles product recommendation logic in the
 * e-commerce backend.
 * It interacts with external APIs and internal data sources to generate
 * personalized product recommendations for users.
 */

@Service
public class RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final ProductRepository productRepository;

    @Value("${recommendation.service.endpoint}")
    private String recommendationServiceEndpoint;

    public RecommendationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Get personalized product recommendations for a specific user.
     *
     * @param userId The ID of the user for whom to retrieve recommendations.
     * @return A list of recommended products.
     */
    public List<Product> getRecommendationsForUser(String userId) {
        try {
            String url = recommendationServiceEndpoint + "/recommend?userId=" + userId;
            logger.info("Calling R recommendation service at: {}", url);

            // Step 1: Call R API (expects JSON array of product IDs)
            ResponseEntity<String[]> response =
                    restTemplate.getForEntity(url, String[].class);

            String[] productIds = response.getBody();

            if (productIds == null || productIds.length == 0) {
                logger.warn("No recommendations found for user {}", userId);
                return Collections.emptyList();
            }

            // Step 2: Fetch products from DB using IDs

            List<Long> productIdsAsLong = Arrays.stream(productIds)
                                    .map(Long::valueOf)
                                    .collect(Collectors.toList());

            List<Product> recommendedProducts =
                    productRepository.findAllById(productIdsAsLong);

            return recommendedProducts;

        } catch (Exception e) {
            logger.error("Error while fetching recommendations for user {}: {}", userId, e.getMessage());
            return Collections.emptyList(); // fallback to empty list
        }
    }
}
