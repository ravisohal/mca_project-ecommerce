package com.sohal.mca.project.ecommerce_backend.controller;

import com.sohal.mca.project.ecommerce_backend.model.User;
import com.sohal.mca.project.ecommerce_backend.model.Address;
import com.sohal.mca.project.ecommerce_backend.service.UserService;
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
 * Description: This controller handles user-related operations such as registration, retrieval, updating, and deletion of user accounts.
 * It uses the UserService to perform business logic and interacts with the User model.
 * The controller provides endpoints for user registration, fetching all users, fetching a user by ID,
 * fetching a user by username, updating user details, and deleting a user account.
 * It includes Swagger annotations for API documentation and uses Spring Security for access control.
 * The controller is annotated with @RestController and @RequestMapping to define the base path for
 * user-related endpoints. The @Tag annotation is used to categorize the controller in the API documentation.
 * The controller uses a logger to log user operations and errors.
 */

@RestController
@RequestMapping(ApiConstants.API_V1_BASE_PATH + "/users")
@Tag(name = "Users", description = "Operations related to user accounts")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        logger.info("UserController initialized.");
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account with shipping and billing addresses.")
    @ApiResponse(responseCode = "201", description = "User registered successfully",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "400", description = "Invalid user data, username or email already exists")
    @PostMapping("/register")
    @PreAuthorize("permitAll()") // Registration is public
    public ResponseEntity<User> registerUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User details including username, password, email, phoneNumber, shippingAddress, and billingAddress.", required = true,
                                              content = @Content(schema = @Schema(implementation = UserRegistrationRequest.class, example = "{\"username\":\"testuser\",\"password\":\"password123\",\"email\":\"test@example.com\",\"phoneNumber\":\"1234567890\",\"shippingAddress\":{\"street\":\"123 Main St\",\"city\":\"Anytown\",\"state\":\"ON\",\"postalCode\":\"A1A1A1\",\"country\":\"Canada\"},\"billingAddress\":{\"street\":\"123 Main St\",\"city\":\"Anytown\",\"state\":\"ON\",\"postalCode\":\"A1A1A1\",\"country\":\"Canada\"}}")))
                                              @RequestBody UserRegistrationRequest request) {
        logger.info("Received request to register new user: {}", request.getUsername());
        try {
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setPassword(request.getPassword()); // Raw password, will be hashed by service
            newUser.setEmail(request.getEmail());
            newUser.setPhoneNumber(request.getPhoneNumber());
            newUser.setRole("customer"); // Default role

            User registeredUser = userService.registerUser(newUser, request.getShippingAddress(), request.getBillingAddress());
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Error registering user: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error registering user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DTO for User Registration Request
    @Schema(name = "UserRegistrationRequest", description = "Request body for new user registration")
    static class UserRegistrationRequest {
        @Schema(description = "Unique username for the user", example = "testuser")
        private String username;
        @Schema(description = "Raw password for the user (will be hashed)", example = "password123")
        private String password;
        @Schema(description = "Unique email address for the user", example = "test@example.com")
        private String email;
        @Schema(description = "Phone number of the user", example = "1234567890")
        private String phoneNumber;
        @Schema(description = "Shipping address details")
        private Address shippingAddress;
        @Schema(description = "Billing address details")
        private Address billingAddress;

        // Getters and Setters for UserRegistrationRequest
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public Address getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }
        public Address getBillingAddress() { return billingAddress; }
        public void setBillingAddress(Address billingAddress) { this.billingAddress = billingAddress; }
    }


    @Operation(summary = "Get all users", description = "Retrieve a list of all registered users. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping
    @PreAuthorize("isAuthenticated()") // Only authenticated users can view all users
    public ResponseEntity<List<User>> getAllUsers() {
        logger.debug("Received request to get all users.");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a single user by their unique ID. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "User found",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can view a specific user
    public ResponseEntity<User> getUserById(@Parameter(description = "ID of the user to retrieve") @PathVariable Long id) {
        logger.debug("Received request to get user by ID: {}", id);
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get user by username", description = "Retrieve a single user by their username. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "User found",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/username/{username}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can view a specific user by username
    public ResponseEntity<User> getUserByUsername(@Parameter(description = "Username of the user to retrieve") @PathVariable String username) {
        logger.debug("Received request to get user by username: {}", username);
        Optional<User> user = userService.getUserByUsername(username);
        user.ifPresent(u -> u.setPassword(null)); // Remove password before returning user details
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update an existing user", description = "Update details of an existing user, including addresses. Requires authentication.")
    @ApiResponse(responseCode = "200", description = "User updated successfully",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "400", description = "Invalid user data or address update failed")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can update users
    public ResponseEntity<User> updateUser(@Parameter(description = "ID of the user to update") @PathVariable Long id,
                                           @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated user details. Include full address objects.", required = true,
                                           content = @Content(schema = @Schema(implementation = UserUpdateRequest.class, example = "{\"email\":\"updated@example.com\",\"phoneNumber\":\"0987654321\",\"shippingAddress\":{\"street\":\"456 New St\",\"city\":\"Newtown\",\"state\":\"QC\",\"postalCode\":\"B2B2B2\",\"country\":\"Canada\"},\"billingAddress\":{\"street\":\"456 New St\",\"city\":\"Newtown\",\"state\":\"QC\",\"postalCode\":\"B2B2B2\",\"country\":\"Canada\"}}")))
                                           @RequestBody UserUpdateRequest request) {
        logger.info("Received request to update user ID: {}", id);
        try {
            // Create a temporary User object for basic details
            User userDetails = new User();
            userDetails.setEmail(request.getEmail());
            userDetails.setPhoneNumber(request.getPhoneNumber());

            User updatedUser = userService.updateUser(id, userDetails, request.getShippingAddress(), request.getBillingAddress());
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            logger.error("Error updating user ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error updating user ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DTO for User Update Request
    @Schema(name = "UserUpdateRequest", description = "Request body for updating existing user details")
    static class UserUpdateRequest {
        @Schema(description = "New email address for the user", example = "updated@example.com")
        private String email;
        @Schema(description = "New phone number for the user", example = "0987654321")
        private String phoneNumber;
        @Schema(description = "Updated shipping address details")
        private Address shippingAddress;
        @Schema(description = "Updated billing address details")
        private Address billingAddress;

        // Getters and Setters for UserUpdateRequest
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public Address getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }
        public Address getBillingAddress() { return billingAddress; }
        public void setBillingAddress(Address billingAddress) { this.billingAddress = billingAddress; }
    }


    @Operation(summary = "Delete a user", description = "Delete a user account by its ID. Requires authentication.")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can delete users
    public ResponseEntity<Void> deleteUser(@Parameter(description = "ID of the user to delete") @PathVariable Long id) {
        logger.info("Received request to delete user ID: {}", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting user ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
