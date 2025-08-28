package com.sohal.mca.project.ecommerce_backend.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-24
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: UserRegistrationRequest class for the e-commerce application.
 * Represents the request body for registering a new user.
 * This class can be extended to include additional fields as needed.
 */

@Schema(name = "UserRegistrationRequest", description = "Request body for new user registration")
public final class UserRegistrationRequest {
    @Schema(description = "Unique username for the user", example = "testuser")
    private String username;
    @Schema(description = "Raw password for the user (will be hashed)", example = "password123")
    private String password;
    @Schema(description = "First name of the user", example = "John")
    private String firstname;
    @Schema(description = "Last name of the user", example = "Doe")
    private String lastname;
    @Schema(description = "Unique email address for the user", example = "test@example.com")
    private String email;
    @Schema(description = "Phone number of the user", example = "1234567890")
    private String phoneNumber;
    @Schema(description = "Shipping address details")
    private Address shippingAddress;
    @Schema(description = "Billing address details")
    private Address billingAddress;

    // Getters and Setters for UserRegistrationRequest
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }
}