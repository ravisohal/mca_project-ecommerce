package com.sohal.mca.project.ecommerce_backend.service;

import com.sohal.mca.project.ecommerce_backend.model.Cart;
import com.sohal.mca.project.ecommerce_backend.model.CartItem;
import com.sohal.mca.project.ecommerce_backend.model.Product;
import com.sohal.mca.project.ecommerce_backend.model.User;
import com.sohal.mca.project.ecommerce_backend.repository.CartRepository;
import com.sohal.mca.project.ecommerce_backend.repository.CartItemRepository;
import com.sohal.mca.project.ecommerce_backend.repository.ProductRepository;
import com.sohal.mca.project.ecommerce_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-27
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: CartService class for the e-commerce application.
 * Provides methods to manage the shopping cart, including adding items, removing items,
 * updating quantities, and calculating total price.
 * This service interacts with the CartRepository and CartItemRepository to perform operations on the cart.
 */

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Constructor for CartService.
     * Initializes the repositories used by this service.
     * @param cartRepository Repository for Cart operations.
     * @param cartItemRepository Repository for CartItem operations.
     * @param productRepository Repository for Product operations.
     * @param userRepository Repository for User operations.
     */
    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       ProductRepository productRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        logger.info("CartService initialized.");
    }

    /**
     * Retrieves the cart for a specific user.
     * If the user doesn't have a cart, a new one is created.
     * @param userId The ID of the user.
     * @return The user's cart.
     * @throws IllegalArgumentException if the user is not found.
     */
    @Transactional // Ensures the entire method runs as a single transaction
    public Cart getOrCreateCart(Long userId) {
        logger.debug("Attempting to get or create cart for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found while getting/creating cart.", userId);
                    return new IllegalArgumentException("User not found.");
                });

        Optional<Cart> existingCart = cartRepository.findByUser(user);
        if (existingCart.isPresent()) {
            logger.info("Found existing cart for user ID: {}", userId);
            return existingCart.get();
        } else {
            Cart newCart = new Cart();
            newCart.setUser(user);
            Cart savedCart = cartRepository.save(newCart);
            // The User entity's cart field is mappedBy="user", so the relationship is owned by Cart.
            // Updating user.setCart(savedCart) here is for the in-memory object,
            // but the persistence is handled by the Cart side.
            // No need to userRepository.save(user) again here for the cart relationship.
            logger.info("Created new cart with ID {} for user ID: {}", savedCart.getId(), userId);
            return savedCart;
        }
    }

    /**
     * Adds a product to the user's cart. If the product is already in the cart, its quantity is updated.
     * @param userId The ID of the user.
     * @param productId The ID of the product to add.
     * @param quantity The quantity to add.
     * @return The updated or new CartItem.
     * @throws IllegalArgumentException if user or product not found, or invalid quantity.
     */
    @Transactional
    public CartItem addProductToCart(Long userId, Long productId, Integer quantity) {
        logger.info("Adding product ID {} (qty {}) to cart for user ID: {}", productId, quantity, userId);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        Cart cart = getOrCreateCart(userId); // Ensure cart exists for the user
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Product with ID {} not found while adding to cart.", productId);
                    return new IllegalArgumentException("Product not found.");
                });

        if (product.getStockQuantity() < quantity) {
            logger.warn("Insufficient stock for product ID {}. Requested: {}, Available: {}", productId, quantity, product.getStockQuantity());
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            if (product.getStockQuantity() < newQuantity) {
                logger.warn("Adding product ID {} to cart would exceed stock. Current in cart: {}, Requested: {}, Available: {}", productId, cartItem.getQuantity(), quantity, product.getStockQuantity());
                throw new IllegalArgumentException("Adding this quantity would exceed available stock for " + product.getName());
            }
            cartItem.setQuantity(newQuantity);
            cartItem.setPriceAtAddition(product.getPrice());
            cartItem.setDiscountAtAddition(product.getDiscount());
            cartItem.setTotal(calculateCartItemTotal(product, newQuantity));
            logger.info("Updated quantity for product ID {} in cart ID {}. New quantity: {}", productId, cart.getId(), newQuantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPriceAtAddition(product.getPrice());
            cartItem.setDiscountAtAddition(product.getDiscount());
            cartItem.setTotal(calculateCartItemTotal(product, quantity));
            logger.info("Added new product ID {} to cart ID {} with quantity: {}", productId, cart.getId(), quantity);
        }
        CartItem savedCartItem = cartItemRepository.save(cartItem);
            
        cart.getCartItems().add(savedCartItem); 
        
        cart.setTotalAmount(cart.getCartItems().stream().map(CartItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        cartRepository.save(cart); 
        return savedCartItem;
    }

    /**
     * Updates the quantity of a product in the user's cart.
     * @param userId The ID of the user.
     * @param productId The ID of the product to update.
     * @param newQuantity The new quantity.
     * @return The updated CartItem, or empty Optional if not found.
     * @throws IllegalArgumentException if user or product not found, or invalid quantity.
     */
    @Transactional
    public Optional<CartItem> updateProductQuantityInCart(Long userId, Long productId, Integer newQuantity) {
        logger.info("Updating quantity for product ID {} to {} in cart for user ID: {}", productId, newQuantity, userId);

        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Product with ID {} not found while updating cart item.", productId);
                    return new IllegalArgumentException("Product not found.");
                });

        Optional<CartItem> cartItemOptional = cartItemRepository.findByCartAndProduct(cart, product);

        if (newQuantity <= 0) {
            // If new quantity is 0 or less, remove the item
            cartItemOptional.ifPresent(cartItem -> {
                cartItemRepository.delete(cartItem);
                cart.getCartItems().remove(cartItem); // Remove from collection to trigger @PreUpdate
                cartRepository.save(cart); // Trigger @PreUpdate on Cart
                logger.info("Product ID {} removed from cart ID {} due to quantity zero.", productId, cart.getId());
            });
            return Optional.empty(); // Item is removed
        }


        if (product.getStockQuantity() < newQuantity) {
            logger.warn("Insufficient stock for product ID {}. New quantity requested: {}, Available: {}", productId, newQuantity, product.getStockQuantity());
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }

        return cartItemOptional
                .map(cartItem -> {
                    cartItem.setQuantity(newQuantity);
                    cartItem.setPriceAtAddition(product.getPrice());
                    cartItem.setDiscountAtAddition(product.getDiscount());
                    cartItem.setTotal(calculateCartItemTotal(product, newQuantity));
                    CartItem updatedItem = cartItemRepository.save(cartItem);
                    cart.setTotalAmount(cart.getCartItems().stream().map(CartItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
                    cartRepository.save(cart);
                    logger.info("Quantity for product ID {} in cart ID {} updated to {}.", productId, cart.getId(), newQuantity);
                    return updatedItem;
                });
    }

    /**
     * Removes a product from the user's cart.
     * @param userId The ID of the user.
     * @param productId The ID of the product to remove.
     * @return An Optional containing the removed CartItem, or empty if not found.
     */
    @Transactional
    public Optional<CartItem> removeProductFromCart(Long userId, Long productId) {
        logger.info("Removing product ID {} from cart for user ID: {}", productId, userId);

        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Product with ID {} not found while removing from cart.", productId);
                    return new IllegalArgumentException("Product not found.");
                });

        Optional<CartItem> cartItemOptional = cartItemRepository.findByCartAndProduct(cart, product);
        cartItemOptional.ifPresent(cartItem -> {
            cartItemRepository.delete(cartItem);
            cart.getCartItems().remove(cartItem);
            cart.setTotalAmount(cart.getCartItems().stream().map(CartItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
            cartRepository.save(cart);
            logger.info("Product ID {} removed from cart ID {}.", productId, cart.getId());
        });
        return cartItemOptional;
    }

    /**
     * Clears all items from a user's cart.
     * @param userId The ID of the user.
     */
    @Transactional
    public void clearCart(Long userId) {
        logger.info("Clearing cart for user ID: {}", userId);
        Cart cart = getOrCreateCart(userId);
        if (!cart.getCartItems().isEmpty()) {
            cartItemRepository.deleteAll(cart.getCartItems()); 
            cart.getCartItems().clear(); 
            cart.setTotalAmount(BigDecimal.ZERO);
            cartRepository.save(cart);
            logger.info("Cart ID {} cleared for user ID: {}", cart.getId(), userId);
        } else {
            logger.info("Cart for user ID {} is already empty. No action needed.", userId);
        }
    }

    BigDecimal calculateCartItemTotal(Product product, Integer quantity) {
        if (product.getDiscount() != null && product.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountedPricePerItem = product.getPrice().multiply(BigDecimal.ONE.subtract(product.getDiscount()));
            return discountedPricePerItem.multiply(BigDecimal.valueOf(quantity));
        } else {
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }

}
