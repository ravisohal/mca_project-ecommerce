package com.sohal.mca.project.ecommerce_backend.controller;

import com.sohal.mca.project.ecommerce_backend.model.Cart;
import com.sohal.mca.project.ecommerce_backend.model.CartItem;
import com.sohal.mca.project.ecommerce_backend.service.CartService;
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

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-26
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: This controller handles operations related to user shopping carts,
 * including retrieving the user's cart, adding products to the cart, updating product quantities,
 * removing products from the cart, and clearing the cart. It uses Swagger annotations for API documentation.
 * The controller is annotated with @RestController and @RequestMapping to define the base path for
 * cart-related endpoints. The @Tag annotation is used to categorize the controller in the API documentation.
 * The controller uses a logger to log requests and responses for debugging purposes.
 * The controller uses a CartService to handle business logic related to cart operations.
 * It provides endpoints for getting the user's cart, adding products to the cart, updating product quantities,
 * removing products from the cart, and clearing the cart. Each endpoint is secured with appropriate
 * authorization annotations to ensure that only authenticated users can perform certain actions.
 */

@RestController
@RequestMapping(ApiConstants.API_V1_BASE_PATH + "/carts")
@Tag(name = "Carts", description = "Operations related to user shopping carts")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
        logger.info("CartController initialized.");
    }

    @Operation(summary = "Get user's cart", description = "Retrieve the shopping cart for a specific user.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved cart",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cart.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()") // User must be authenticated to view their cart
    public ResponseEntity<Cart> getUserCart(@Parameter(description = "ID of the user whose cart to retrieve") @PathVariable Long userId) {
        logger.debug("Received request to get cart for user ID: {}", userId);
        try {
            Cart cart = cartService.getOrCreateCart(userId);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            logger.error("Error getting cart for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Unexpected error getting cart for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Add product to cart", description = "Add a specified quantity of a product to the user's cart. Creates cart if it doesn't exist.")
    @ApiResponse(responseCode = "200", description = "Product added/updated in cart",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartItem.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request (e.g., product not found, insufficient stock, invalid quantity)")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()") // User must be authenticated to add to cart
    public ResponseEntity<CartItem> addProductToCart(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body for adding product to cart", required = true,
                                                  content = @Content(schema = @Schema(example = "{\"userId\":1,\"productId\":101,\"quantity\":2}")))
                                                  @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long productId = request.get("productId");
        Integer quantity = request.get("quantity").intValue(); // Convert Long to Integer

        logger.info("Received request to add product ID {} (qty {}) to cart for user ID: {}", productId, quantity, userId);
        try {
            CartItem cartItem = cartService.addProductToCart(userId, productId, quantity);
            return ResponseEntity.ok(cartItem);
        } catch (IllegalArgumentException e) {
            logger.error("Error adding product to cart: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null); // Return 400 with no body or a custom error DTO
        } catch (Exception e) {
            logger.error("Unexpected error adding product to cart: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Update product quantity in cart", description = "Update the quantity of a product in the user's cart.")
    @ApiResponse(responseCode = "200", description = "Product quantity updated in cart",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartItem.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request (e.g., product not found, insufficient stock, invalid quantity)")
    @ApiResponse(responseCode = "404", description = "Cart item not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PutMapping("/update-quantity")
    @PreAuthorize("isAuthenticated()") // User must be authenticated to update cart
    public ResponseEntity<CartItem> updateProductQuantityInCart(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request body for updating product quantity in cart", required = true,
                                                              content = @Content(schema = @Schema(example = "{\"userId\":1,\"productId\":101,\"newQuantity\":3}")))
                                                              @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long productId = request.get("productId");
        Integer newQuantity = request.get("newQuantity").intValue(); // Convert Long to Integer

        logger.info("Received request to update product ID {} quantity to {} for user ID: {}", productId, newQuantity, userId);
        try {
            Optional<CartItem> updatedCartItem = cartService.updateProductQuantityInCart(userId, productId, newQuantity);
            return updatedCartItem.map(ResponseEntity::ok)
                                  .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            logger.error("Error updating product quantity in cart: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Unexpected error updating product quantity in cart: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Remove product from cart", description = "Remove a specific product from the user's cart.")
    @ApiResponse(responseCode = "204", description = "Product removed from cart successfully")
    @ApiResponse(responseCode = "404", description = "Cart item or product not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @DeleteMapping("/remove")
    @PreAuthorize("isAuthenticated()") // User must be authenticated to remove from cart
    public ResponseEntity<Void> removeProductFromCart(@Parameter(description = "ID of the user") @RequestParam Long userId,
                                                    @Parameter(description = "ID of the product to remove") @RequestParam Long productId) {
        logger.info("Received request to remove product ID {} from cart for user ID: {}", productId, userId);
        try {
            Optional<CartItem> removedItem = cartService.removeProductFromCart(userId, productId);
            if (removedItem.isPresent()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            logger.error("Error removing product from cart: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error removing product from cart: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Clear user's cart", description = "Remove all items from the user's shopping cart.")
    @ApiResponse(responseCode = "204", description = "Cart cleared successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @DeleteMapping("/clear/{userId}")
    @PreAuthorize("isAuthenticated()") // User must be authenticated to clear their cart
    public ResponseEntity<Void> clearCart(@Parameter(description = "ID of the user whose cart to clear") @PathVariable Long userId) {
        logger.info("Received request to clear cart for user ID: {}", userId);
        try {
            cartService.clearCart(userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Error clearing cart for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Unexpected error clearing cart for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
