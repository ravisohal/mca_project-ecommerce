package com.sohal.mca.project.ecommerce_backend.service;

import com.sohal.mca.project.ecommerce_backend.model.User;
import com.sohal.mca.project.ecommerce_backend.model.Address;
import com.sohal.mca.project.ecommerce_backend.model.Cart; 
import com.sohal.mca.project.ecommerce_backend.repository.UserRepository;
import com.sohal.mca.project.ecommerce_backend.repository.AddressRepository;
import com.sohal.mca.project.ecommerce_backend.repository.CartRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // For password hashing
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-27
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: UserService class for the e-commerce application.
 * Provides methods to manage users, including fetching, searching, saving, and deleting users.
 * This service interacts with the UserRepository to perform CRUD operations.
 */

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository; // Inject AddressRepository
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for UserService.
     * Initializes the service with the required repositories.
     * @param userRepository The repository for user operations.
     * @param cartRepository The repository for cart operations.
     * @param addressRepository The repository for address operations.
     * @param passwordEncoder The password encoder for hashing passwords.
     */
    @Autowired
    public UserService(UserRepository userRepository, CartRepository cartRepository,
                       AddressRepository addressRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
        logger.info("UserService initialized.");
    }

    /**
     * Registers a new user with a hashed password and creates an empty cart for them.
     * Requires shipping and billing addresses to be provided.
     * @param user The user object to register (username, email, raw password).
     * @param shippingAddress The shipping address details.
     * @param billingAddress The billing address details.
     * @return The saved user.
     * @throws IllegalArgumentException if username or email already exists, or address details are invalid.
     */
    @Transactional
    public User registerUser(User user, Address shippingAddress, Address billingAddress) {
        logger.info("Attempting to register new user: {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            logger.warn("User registration failed: Username '{}' already exists.", user.getUsername());
            throw new IllegalArgumentException("Username already exists.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("User registration failed: Email '{}' already exists.", user.getEmail());
            throw new IllegalArgumentException("Email already exists.");
        }

        // Save addresses first as they are ManyToOne relationships
        Address savedShippingAddress = addressRepository.save(shippingAddress);
        Address savedBillingAddress = addressRepository.save(billingAddress);

        user.setShippingAddress(savedShippingAddress);
        user.setBillingAddress(savedBillingAddress);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash the password
        User savedUser = userRepository.save(user);

        // Create an empty cart for the new user
        Cart newCart = new Cart();
        newCart.setUser(savedUser);
        cartRepository.save(newCart);
        savedUser.setCart(newCart); // Link the cart back to the user object (for in-memory representation)

        logger.info("User registered successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Authenticates a user by username and password.
     * @param username The username.
     * @param password The raw password.
     * @return An Optional containing the user if authentication is successful, or empty if not.
     */
    public Optional<User> authenticateUser(String username, String password) {
        logger.debug("Attempting to authenticate user: {}", username);
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword())) // Compare raw password with hashed one
                .map(user -> {
                    logger.info("User '{}' authenticated successfully.", username);
                    return user;
                })
                .or(() -> {
                    logger.warn("Authentication failed for user: {}", username);
                    return Optional.empty();
                });
    }

    /**
     * Retrieves all users from the database.
     * @return A list of all users.
     */
    public List<User> getAllUsers() {
        logger.debug("Attempting to retrieve all users.");
        List<User> users = userRepository.findAll();
        logger.info("Retrieved {} users.", users.size());
        return users;
    }

    /**
     * Retrieves a user by their ID.
     * @param id The ID of the user.
     * @return An Optional containing the user if found, or empty if not.
     */
    public Optional<User> getUserById(Long id) {
        logger.debug("Attempting to retrieve user with ID: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.info("User with ID {} found: {}", id, user.get().getUsername());
        } else {
            logger.warn("User with ID {} not found.", id);
        }
        return user;
    }
    
    /**
     * Retrieves a user by their username.
     * @param username The username of the user.
     * @return An Optional containing the user if found, or empty if not.
     */
    public Optional<User> getUserByUsername(String username) {
        logger.debug("Attempting to retrieve user by username: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            logger.info("Found user with username: {}", username);
        } else {
            logger.warn("User with username {} not found.", username);
        }
        return user;
    }

    /**
     * Updates an existing user's details, including addresses.
     * @param id The ID of the user to update.
     * @param userDetails The updated user details (email, phoneNumber).
     * @param shippingAddressDetails Updated shipping address details.
     * @param billingAddressDetails Updated billing address details.
     * @return The updated user, or null if the user was not found.
     * @throws IllegalArgumentException if user not found or address update fails.
     */
    @Transactional
    public User updateUser(Long id, User userDetails, Address shippingAddressDetails, Address billingAddressDetails) {
        logger.info("Attempting to update user with ID: {}", id);
        return userRepository.findById(id)
                .map(existingUser -> {
                    // Update basic user details
                    existingUser.setEmail(userDetails.getEmail());
                    existingUser.setPhoneNumber(userDetails.getPhoneNumber());

                    // Update shipping address
                    Address existingShippingAddress = existingUser.getShippingAddress();
                    if (existingShippingAddress != null) {
                        existingShippingAddress.setStreet(shippingAddressDetails.getStreet());
                        existingShippingAddress.setCity(shippingAddressDetails.getCity());
                        existingShippingAddress.setState(shippingAddressDetails.getState());
                        existingShippingAddress.setPostalCode(shippingAddressDetails.getPostalCode());
                        existingShippingAddress.setCountry(shippingAddressDetails.getCountry());
                        addressRepository.save(existingShippingAddress);
                    } else {
                        // If no existing shipping address, save the new one and link it
                        Address newShippingAddress = addressRepository.save(shippingAddressDetails);
                        existingUser.setShippingAddress(newShippingAddress);
                    }

                    // Update billing address
                    Address existingBillingAddress = existingUser.getBillingAddress();
                    if (existingBillingAddress != null) {
                        existingBillingAddress.setStreet(billingAddressDetails.getStreet());
                        existingBillingAddress.setCity(billingAddressDetails.getCity());
                        existingBillingAddress.setState(billingAddressDetails.getState());
                        existingBillingAddress.setPostalCode(billingAddressDetails.getPostalCode());
                        existingBillingAddress.setCountry(billingAddressDetails.getCountry());
                        addressRepository.save(existingBillingAddress);
                    } else {
                        // If no existing billing address, save the new one and link it
                        Address newBillingAddress = addressRepository.save(billingAddressDetails);
                        existingUser.setBillingAddress(newBillingAddress);
                    }

                    // Password update should be handled by a separate method for security
                    User updatedUser = userRepository.save(existingUser);
                    logger.info("User with ID {} updated.", id);
                    return updatedUser;
                })
                .orElseGet(() -> {
                    logger.warn("User with ID {} not found for update.", id);
                    return null;
                });
    }

    /**
     * Deletes a user by their ID. This will also cascade delete their cart.
     * @param id The ID of the user to delete.
     */
    @Transactional
    public void deleteUser(Long id) {
        logger.info("Attempting to delete user with ID: {}", id);
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Due to cascadeType.ALL and orphanRemoval=true on User's cart,
            // deleting the user will also delete their cart.
            // Addresses are @ManyToOne, so they won't be deleted automatically unless no other user references them.
            userRepository.delete(user);
            logger.info("User with ID {} deleted successfully.", id);
        } else {
            logger.warn("User with ID {} not found for deletion.", id);
        }
    }

}
