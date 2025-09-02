package com.sohal.mca.project.ecommerce_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.sohal.mca.project.ecommerce_backend.model.Product;
import com.sohal.mca.project.ecommerce_backend.service.RecommendationService;
import com.sohal.mca.project.ecommerce_backend.util.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Ravi Sohal
 * Date: 2025-08-15
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: This controller handles product recommendation operations in the
 * e-commerce backend.
 * It provides endpoints for retrieving personalized product recommendations
 * for users based on their preferences and behavior.
 * The controller uses Swagger annotations for API documentation and includes
 * security annotations to protect the endpoints.
 */

@RestController
@RequestMapping(ApiConstants.API_V1_BASE_PATH + "/recommendations")
@Tag(name = "Recommendations", description = "Operations related to product recommendations in the e-commerce store")
public class RecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        logger.info("RecommendationController initialized.");
        this.recommendationService = recommendationService;
    }

    @Operation(summary = "Get recommendations for a user", description = "Retrieve personalized product recommendations for a specific user.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of product recommendations", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request (e.g., user not found, insufficient data to generate recommendations)")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/{userId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> getRecommendations(@PathVariable String userId) {
        logger.info("Fetching recommendations for user: {}", userId);
        
        List<Product> recommendations = recommendationService.getRecommendationsForUser(userId);
        return ResponseEntity.ok(recommendations);
    }

}
