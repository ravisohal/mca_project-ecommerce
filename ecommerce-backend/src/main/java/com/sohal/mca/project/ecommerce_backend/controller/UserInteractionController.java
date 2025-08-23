package com.sohal.mca.project.ecommerce_backend.controller;

import com.sohal.mca.project.ecommerce_backend.model.InteractionType;
import com.sohal.mca.project.ecommerce_backend.model.UserInteraction;
import com.sohal.mca.project.ecommerce_backend.service.UserInteractionService;
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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: This controller handles user interactions with products,
 * including logging interactions (e.g., view, add to cart, purchase) and retrieving
 * interactions for recommendation purposes. It uses Swagger annotations for API documentation.
 * The controller is annotated with @RestController and @RequestMapping to define the base path for
 * interaction-related endpoints. The @Tag annotation is used to categorize the controller in the API documentation
 * and provide a description of its purpose.
 * The controller uses a logger to log requests and responses for debugging purposes.
 * It uses a UserInteractionService to handle business logic related to user interactions.
 * The controller provides endpoints for logging user interactions, retrieving all interactions for a user,
 * and filtering interactions by type (e.g., VIEW, ADD_TO_CART, PURCHASE).
 * Each endpoint is secured with appropriate authorization annotations to ensure that only authenticated users
 * can perform certain actions.
 */

@RestController
@RequestMapping(ApiConstants.API_V1_BASE_PATH + "/interactions")
@Tag(name = "User Interactions", description = "Logging and retrieving user interactions with products for recommendation engine")
public class UserInteractionController {

    private static final Logger logger = LoggerFactory.getLogger(UserInteractionController.class);

    private final UserInteractionService userInteractionService;

    @Autowired
    public UserInteractionController(UserInteractionService userInteractionService) {
        this.userInteractionService = userInteractionService;
        logger.info("UserInteractionController initialized.");
    }

    @Operation(summary = "Log a user interaction", description = "Records a user's interaction (e.g., view, add to cart, purchase) with a product.")
    @ApiResponse(responseCode = "201", description = "Interaction logged successfully",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInteraction.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request (e.g., user or product not found, invalid interaction type)")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PostMapping("/log")
    @PreAuthorize("isAuthenticated()") // User must be authenticated to log interactions
    public ResponseEntity<UserInteraction> logInteraction(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body for logging user interaction", required = true,
                                                        content = @Content(schema = @Schema(example = "{\"userId\":1,\"productId\":101,\"interactionType\":\"VIEW\"}")))
                                                        @RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long productId = Long.valueOf(request.get("productId").toString());
        InteractionType interactionType = InteractionType.valueOf((String) request.get("interactionType"));

        logger.info("Received request to log interaction for user ID: {}, product ID: {}, type: {}", userId, productId, interactionType);
        try {
            UserInteraction loggedInteraction = userInteractionService.logUserInteraction(userId, productId, interactionType);
            return new ResponseEntity<>(loggedInteraction, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Error logging interaction: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Unexpected error logging interaction: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get all interactions for a user", description = "Retrieve all recorded interactions for a specific user. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user interactions",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInteraction.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can view their interactions
    public ResponseEntity<List<UserInteraction>> getInteractionsByUser(@Parameter(description = "ID of the user whose interactions to retrieve") @PathVariable Long userId) {
        logger.debug("Received request to get interactions for user ID: {}", userId);
        try {
            List<UserInteraction> interactions = userInteractionService.getInteractionsByUser(userId);
            return ResponseEntity.ok(interactions);
        } catch (IllegalArgumentException e) {
            logger.error("Error getting interactions for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Unexpected error getting interactions for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get interactions of a specific type for a user", description = "Retrieve interactions filtered by type (e.g., VIEW, PURCHASE) for a specific user. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered user interactions",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInteraction.class)))
    @ApiResponse(responseCode = "400", description = "Invalid interaction type")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/user/{userId}/type/{interactionType}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can view their interactions by type
    public ResponseEntity<List<UserInteraction>> getInteractionsByUserAndType(
            @Parameter(description = "ID of the user whose interactions to retrieve") @PathVariable Long userId,
            @Parameter(description = "Type of interaction to filter by (e.g., VIEW, ADD_TO_CART, PURCHASE)", example = "PURCHASE") @PathVariable InteractionType interactionType) {
        logger.debug("Received request to get interactions for user ID: {} by type: {}", userId, interactionType);
        try {
            List<UserInteraction> interactions = userInteractionService.getInteractionsByUserAndType(userId, interactionType);
            return ResponseEntity.ok(interactions);
        } catch (IllegalArgumentException e) {
            logger.error("Error getting interactions by type for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error getting interactions by type for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
