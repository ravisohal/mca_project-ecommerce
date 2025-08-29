package com.sohal.mca.project.ecommerce_backend.service;

import com.sohal.mca.project.ecommerce_backend.model.UserInteraction;
import com.sohal.mca.project.ecommerce_backend.model.User;
import com.sohal.mca.project.ecommerce_backend.model.Product;
import com.sohal.mca.project.ecommerce_backend.model.InteractionType; 
import com.sohal.mca.project.ecommerce_backend.repository.UserInteractionRepository;
import com.sohal.mca.project.ecommerce_backend.repository.UserRepository;
import com.sohal.mca.project.ecommerce_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-27
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: UserInteractionService class for managing user interactions with products.
 * This service logs user interactions and retrieves interaction data for recommendation engines.
 * It interacts with UserInteractionRepository, UserRepository, and ProductRepository to perform operations.
 * Provides methods to log interactions, retrieve all interactions for a user, and filter interactions by type
 */

@Service
public class UserInteractionService {

    private static final Logger logger = LoggerFactory.getLogger(UserInteractionService.class);

    private final UserInteractionRepository userInteractionRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * Constructor for UserInteractionService.
     * @param userInteractionRepository The repository to interact with user interactions.
     * @param userRepository The repository to interact with users.
     * @param productRepository The repository to interact with products.
     */
    @Autowired
    public UserInteractionService(UserInteractionRepository userInteractionRepository,
                                  UserRepository userRepository, ProductRepository productRepository) {
        this.userInteractionRepository = userInteractionRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        logger.info("UserInteractionService initialized.");
    }

    /**
     * Logs a user's interaction with a product. This data is crucial for the recommendation engine.
     * @param userId The ID of the user.
     * @param productId The ID of the product.
     * @param interactionType The type of interaction (e.g., VIEW, ADD_TO_CART, PURCHASE).
     * @return The saved UserInteraction object.
     * @throws IllegalArgumentException if user or product not found.
     */
    @Transactional
    public UserInteraction logUserInteraction(Long userId, Long productId, InteractionType interactionType) {
        logger.debug("Logging interaction: User ID {}, Product ID {}, Type: {}", userId, productId, interactionType);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found for interaction logging.", userId);
                    return new IllegalArgumentException("User not found.");
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Product with ID {} not found for interaction logging.", productId);
                    return new IllegalArgumentException("Product not found.");
                });

        UserInteraction interaction = new UserInteraction();
        interaction.setUser(user);
        interaction.setProduct(product);
        interaction.setInteractionType(interactionType);
        // timestamp is set by @PrePersist

        UserInteraction savedInteraction = userInteractionRepository.save(interaction);
        logger.info("User interaction logged: ID {}, User {}, Product {}, Type {}",
                    savedInteraction.getId(), userId, productId, interactionType);
        return savedInteraction;
    }

    /**
     * Retrieves all interactions for a specific user.
     * This data would be fed into the recommendation engine.
     * @param userId The ID of the user.
     * @return A list of UserInteraction objects for the given user.
     * @throws IllegalArgumentException if user not found.
     */
    public List<UserInteraction> getInteractionsByUser(Long userId) {
        logger.debug("Retrieving interactions for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found while fetching interactions.", userId);
                    return new IllegalArgumentException("User not found.");
                });
        List<UserInteraction> interactions = userInteractionRepository.findByUser(user);
        logger.info("Found {} interactions for user ID: {}", interactions.size(), userId);
        return interactions;
    }

    /**
     * Retrieves interactions of a specific type for a user.
     * @param userId The ID of the user.
     * @param interactionType The type of interaction (enum).
     * @return A list of UserInteraction objects.
     * @throws IllegalArgumentException if user not found.
     */
    public List<UserInteraction> getInteractionsByUserAndType(Long userId, InteractionType interactionType) {
        logger.debug("Retrieving '{}' interactions for user ID: {}", interactionType, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found while fetching interactions by type.", userId);
                    return new IllegalArgumentException("User not found.");
                });
        List<UserInteraction> interactions = userInteractionRepository.findByUserAndInteractionType(user, interactionType);
        logger.info("Found {} '{}' interactions for user ID: {}", interactions.size(), interactionType, userId);
        return interactions;
    }

}
