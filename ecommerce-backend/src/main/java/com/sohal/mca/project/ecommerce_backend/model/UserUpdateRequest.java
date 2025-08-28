package com.sohal.mca.project.ecommerce_backend.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Author: Ravi Sohal
 * Date: 2025-07-24
 * Post: MCA Project - E-commerce Backend
 * Version: 1.0
 * Description: UserUpdateRequest class for the e-commerce application.
 * Represents the request body for updating existing user details.
 * This class can be extended to include additional fields as needed.
 */

@Schema(name = "UserUpdateRequest", description = "Request body for updating existing user details")
public final class UserUpdateRequest {
    @Schema(description = "First name of the user", example = "John")
    private String firstname;
    @Schema(description = "Last name of the user", example = "Doe")
    private String lastname;
    @Schema(description = "New email address for the user", example = "updated@example.com")
    private String email;
    @Schema(description = "New phone number for the user", example = "0987654321")
    private String phoneNumber;
    @Schema(description = "Updated shipping address details")
    private Address shippingAddress;
    @Schema(description = "Updated billing address details")
    private Address billingAddress;

    // Getters and Setters for UserUpdateRequest
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
